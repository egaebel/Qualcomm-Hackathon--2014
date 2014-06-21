package main

import (
	"os"
	"log"
	"net"
	"fmt"
)

type Demand struct {
	demand_size int32
	groupID int32
	group_size int32
	chunk_size int32
	filename string
}

//Sets the maximum size of a buffer for a demand
const BUFFER_LENGTH = 1024

func main() {
	// Listen on TCP port 80 on all interfaces.
	l, err := net.Listen("tcp", ":8080")
	if err != nil {
		log.Fatal(err)
	}
	defer l.Close()
	for {
		// Wait for a connection.
		conn, err := l.Accept()

		fmt.Println("Opening a new connection")

		if err != nil {
			log.Fatal(err)
		}
		// Handle the connection in a new goroutine.
		// The loop then returns to accepting, so that
		// multiple connections may be served concurrently.
		go handle_connection(conn)
	}
}

//Handles an incoming network connection
//Currently echos data and writes to standard out
//TO DO assign a chunk number and write back the chunk number,
//      size of chunk, and the chunk
func handle_connection(c net.Conn) {
	//Ensures the connection
	defer c.Close()

	var buf [BUFFER_LENGTH]byte

	n, err := c.Read(buf[0:])

	if err != nil {
        log.Fatal(err)
    }

    //Echo whatever was written so the client knows its information was recieved
	_, err2 := c.Write(buf[0:n])

	if err2 != nil {
		log.Fatal(err2)
	}

	//Print out the information for debugging purposes
	os.Stdout.Write(buf[0:n])

	test_file, err_open := os.Open("/home/pi/files_for_hackathon/test")
	if err_open != nil {
		log.Fatal(err_open)
		fmt.Println("Failed to open file.")
	}
	
	n2, err_file_read := test_file.Read(buf[0:])
	if err_file_read != nil {
		log.Fatal(err_file_read)
		fmt.Println("Failed to read file.")
	}

	_, err_file_write_to_client := c.Write(buf[0:n2])
	if err_file_write_to_client != nil {
		log.Fatal(err_file_write_to_client)
		fmt.Println("Failed to write file to client.")
	}

	os.Stdout.Write(buf[0:n2])

}


//Converts a C string stored in a []byte to a Go string
func CToGoString(c []byte) string {
    n := -1
    for i, b := range c {
        if b == 0 {
            break
        }
        n = i
    }
    return string(c[:n+1])
}

func parse_demand(d *Demand, buf []byte) {

}