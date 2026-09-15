package main

import "sync"

var activeCharacters = map[string]string{}
var activeMu sync.Mutex

func setActiveCharacter(token, characterID string) {
    activeMu.Lock()
    activeCharacters[token] = characterID
    activeMu.Unlock()
}

func getActiveCharacter(token string) (string, bool) {
    activeMu.Lock()
    characterID, ok := activeCharacters[token]
    activeMu.Unlock()
    return characterID, ok
}
