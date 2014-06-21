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

type Group struct {
	groupID int32
	num_clients int32
	current_file_location int64
	file_size int64
	current_client_num int32
}

var gr *Group
var mut *Mutex

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
	defer file.Close()
	if err != nil {
		log.Fatal("Couldn't open file in HandleConnection", err)
	}
	mut.Lock()
	if gr.num_clients == 0 {
		gr.groupID = demand.groupID
		gr.num_clients = demand.group_size
	}
	read_location := gr.current_file_location
	gr.current_file_location = gr.current_file_location + demand.chunk_size
	gr.current_client_num = gr.current_client_num + 1
	mut.Unlock()
	
	//Keep track of how much has been read
	n := 1
	total_read := 0
	themBytes := make([]byte, 1024)

	//loop until we've read equal to the chunksize or the end of the file
	for total_read < demand.chunk_size && n != 0 {
		n, _ = file.ReadAt(themBytes, read_location + total_read)
		fmt.Println("Writing them BYtes...awww yeahhh")
		w.Write(themBytes)
	}
}

//Initializes a group

func main() {
	gr = new(Group)
	mut = new(Mutex)

	http.HandleFunc("/", HandleConnection)
	err := http.ListenAndServe(":8080", nil)
	if err != nil {

		log.Fatal("Listen and serve hates us....", err)
	}
}