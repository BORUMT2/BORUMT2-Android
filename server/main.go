package main

import (
	"bufio"
	"encoding/json"
	"fmt"
	"net"
	"os"
	"strconv"
	"sync"
	"time"

	"golang.org/x/crypto/bcrypt"
)

type Account struct {
	Username string `json:"username"`
	Password string `json:"password"`
}

var accounts = map[string]Account{}
var characters = map[string]Character{}
var mu sync.Mutex

func newCharacterID() string {
	return strconv.FormatInt(time.Now().UnixNano(), 10)
}

func loadCharacters() {
	data, err := os.ReadFile("data/characters.json")
	if err != nil {
		return
	}
	_ = json.Unmarshal(data, &characters)
}

func loadAccounts() {
	data, err := os.ReadFile("data/accounts.json")
	if err != nil {
		return
	}
	_ = json.Unmarshal(data, &accounts)
}

func saveCharacters() {
	data, _ := json.MarshalIndent(characters, "", "  ")
	_ = os.WriteFile("data/characters.json", data, 0600)
}

func saveAccounts() {
	data, _ := json.MarshalIndent(accounts, "", "  ")
	_ = os.WriteFile("data/accounts.json", data, 0600)
}

func handleConnection(conn net.Conn) {
	defer conn.Close()
	scanner := bufio.NewScanner(conn)
	for scanner.Scan() {
		var req map[string]string
		if json.Unmarshal(scanner.Bytes(), &req) != nil {
			fmt.Fprintln(conn, `{"ok":false,"error":"INVALID_JSON"}`)
			continue
		}
		switch req["command"] {
		case "PING":
			fmt.Fprintln(conn, `{"ok":true,"response":"PONG"}`)
		case "REGISTER":
			mu.Lock()
			if _, exists := accounts[req["username"]]; exists {
				mu.Unlock()
				fmt.Fprintln(conn, `{"ok":false,"error":"ACCOUNT_EXISTS"}`)
				continue
			}
			hash, err := bcrypt.GenerateFromPassword([]byte(req["password"]), bcrypt.DefaultCost)
			if err != nil {
				mu.Unlock()
				fmt.Fprintln(conn, `{"ok":false,"error":"PASSWORD_HASH_FAILED"}`)
				continue
			}
			accounts[req["username"]] = Account{Username: req["username"], Password: string(hash)}
			saveAccounts()
			mu.Unlock()
			fmt.Fprintln(conn, `{"ok":true,"response":"REGISTERED"}`)
		case "CREATE_CHARACTER":
			username := req["username"]
			name := req["name"]
			className := req["class"]
			gender := req["gender"]
			if username == "" || name == "" || className == "" || gender == "" {
				fmt.Fprintln(conn, `{"ok":false,"error":"MISSING_FIELDS"}`)
				continue
			}
			if !validCharacterName(name) {
				fmt.Fprintln(conn, `{"ok":false,"error":"INVALID_CHARACTER_NAME"}`)
				continue
			}
			if !validClasses[className] {
				fmt.Fprintln(conn, `{"ok":false,"error":"INVALID_CLASS"}`)
				continue
			}
			if !validGenders[gender] {
				fmt.Fprintln(conn, `{"ok":false,"error":"INVALID_GENDER"}`)
				continue
			}
			mu.Lock()
			if _, exists := accounts[username]; !exists {
				mu.Unlock()
				fmt.Fprintln(conn, `{"ok":false,"error":"ACCOUNT_NOT_FOUND"}`)
				continue
			}
			nameExists := false
			for _, existing := range characters {
				if existing.Name == name {
					nameExists = true
					break
				}
			}
			if nameExists {
				mu.Unlock()
				fmt.Fprintln(conn, `{"ok":false,"error":"CHARACTER_NAME_EXISTS"}`)
				continue
			}
			character := newCharacter(newCharacterID(), username, name, className, gender)
			characters[character.ID] = character
			saveCharacters()
			mu.Unlock()
			fmt.Fprintln(conn, `{"ok":true,"response":"CHARACTER_CREATED"}`)
			continue
		case "LIST_CHARACTERS":
			token := req["session_token"]
			username, valid := getSessionUser(token)
			if !valid {
				fmt.Fprintln(conn, `{"ok":false,"error":"INVALID_SESSION"}`)
				continue
			}
			mu.Lock()
			list := []Character{}
			for _, character := range characters {
				if character.Account == username {
					list = append(list, character)
				}
			}
			mu.Unlock()
			response, _ := json.Marshal(map[string]interface{}{"ok": true, "characters": list})
			fmt.Fprintln(conn, string(response))
			continue

		case "SELECT_CHARACTER":
			token := req["session_token"]
			username, valid := getSessionUser(token)
			if !valid {
				fmt.Fprintln(conn, `{"ok":false,"error":"INVALID_SESSION"}`)
				continue
			}
			characterID := req["character_id"]
			mu.Lock()
			character, exists := characters[characterID]
			if !exists || character.Account != username {
				mu.Unlock()
				fmt.Fprintln(conn, `{"ok":false,"error":"CHARACTER_NOT_FOUND"}`)
				continue
			}
			mu.Unlock()
			setActiveCharacter(token, characterID)
			response, _ := json.Marshal(map[string]interface{}{"ok": true, "character": character})
			fmt.Fprintln(conn, string(response))
			continue
		case "GET_CHARACTER_STATE":
			token := req["session_token"]
			_, valid := getSessionUser(token)
			if !valid {
				fmt.Fprintln(conn, `{"ok":false,"error":"INVALID_SESSION"}`)
				continue
			}
			characterID, active := getActiveCharacter(token)
			if !active {
				fmt.Fprintln(conn, `{"ok":false,"error":"NO_ACTIVE_CHARACTER"}`)
				continue
			}
			mu.Lock()
			character, exists := characters[characterID]
			mu.Unlock()
			if !exists {
				fmt.Fprintln(conn, `{"ok":false,"error":"CHARACTER_NOT_FOUND"}`)
				continue
			}
			setActiveCharacter(token, characterID)
			response, _ := json.Marshal(map[string]interface{}{"ok": true, "character": character})
			fmt.Fprintln(conn, string(response))
			continue
		case "LOGIN":
			mu.Lock()
			acc, exists := accounts[req["username"]]
			mu.Unlock()
			if !exists || bcrypt.CompareHashAndPassword([]byte(acc.Password), []byte(req["password"])) != nil {
				fmt.Fprintln(conn, `{"ok":false,"error":"LOGIN_FAILED"}`)
				continue
			}
			token := newSession(req["username"])
			response, _ := json.Marshal(map[string]interface{}{"ok": true, "response": "LOGIN_OK", "session_token": token})
			fmt.Fprintln(conn, string(response))
		default:
			fmt.Fprintln(conn, `{"ok":false,"error":"UNKNOWN_COMMAND"}`)
		}
	}
}

func main() {
	os.MkdirAll("data", 0755)
	loadAccounts()
	loadCharacters()
	listener, err := net.Listen("tcp", ":5000")
	if err != nil {
		panic(err)
	}
	defer listener.Close()
	fmt.Println("BÖRÜMT2 Server :5000 üzerinde çalışıyor.")
	for {
		conn, err := listener.Accept()
		if err != nil {
			continue
		}
		go handleConnection(conn)
	}
}
