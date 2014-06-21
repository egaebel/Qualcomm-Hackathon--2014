package main

import (
	"os"
	"log"
	"strings"
	"strconv"
	"fmt"
	"net/http"
)

type Demand struct {
	groupID int32
	group_size int32
	chunk_size int64
	filename string
}

//Sets the maximum size of a buffer for a demand
const BUFFER_LENGTH = 1024

func HandleConnection(w http.ResponseWriter, req *http.Request) {

	fmt.Println("Why hello there, let me handle this!")

	fmt.Println("Wtf is this Request..." + req.URL.String())

	//Split on dashes
	args := strings.Split(req.URL.String(), "-")
	if len(args) != 4 {
		fmt.Println("Invalid number of args")
	}

	//Parse out args
	groupId, err := strconv.ParseInt(args[0], 10, 32)
	groupSize, err := strconv.ParseInt(args[1], 10, 32)
	chunkSize, err := strconv.ParseInt(args[2], 10, 64)
	filename := args[3]

	//Construct demand struct
	demand := new(Demand)
	demand.groupID = int32(groupId)
	demand.group_size = int32(groupSize)
	demand.chunk_size = chunkSize

	//Open filename
	file, err := os.Open(filename)
	if err != nil {
		log.Fatal("Couldn't open file in HandleConnection", err)
	}


	themBytes := make([]byte, 1024)
	file.ReadAt(themBytes, 0)

	fmt.Println("Writing them BYtes...awww yeahhh")
	w.Write(themBytes)
}

func main() {

	http.HandleFunc("/", HandleConnection)
	err := http.ListenAndServe(":8080", nil)
	if err != nil {

		log.Fatal("Listen and serve hates us....", err)
	}
}