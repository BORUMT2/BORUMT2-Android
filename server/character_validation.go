package main

func validCharacterName(name string) bool {
    count := 0
    for range name { count++ }
    return count >= 2 && count <= 12
}
