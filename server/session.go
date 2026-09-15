package main

import (
    "crypto/rand"
    "encoding/hex"
    "sync"
)

var sessions = map[string]string{}
var sessionMu sync.Mutex

func newSession(username string) string {
    b := make([]byte, 32)
    _, _ = rand.Read(b)
    token := hex.EncodeToString(b)
    sessionMu.Lock()
    sessions[token] = username
    sessionMu.Unlock()
    return token
}

func getSessionUser(token string) (string, bool) {
    sessionMu.Lock()
    username, ok := sessions[token]
    sessionMu.Unlock()
    return username, ok
}
