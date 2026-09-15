package main

type Character struct {
	ID      string `json:"id"`
	Name    string `json:"name"`
	Class   string `json:"class"`
	Gender  string `json:"gender"`
	Level   int    `json:"level"`
	Yang    int64  `json:"yang"`
	Account string `json:"account"`
}

func newCharacter(id, account, name, className, gender string) Character {
	return Character{
		ID:      id,
		Account: account,
		Name:    name,
		Class:   className,
		Gender:  gender,
		Level:   10,
		Yang:    1000000,
	}
}
