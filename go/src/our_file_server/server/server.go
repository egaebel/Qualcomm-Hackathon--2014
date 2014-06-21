package main

import (
	"os"
	"io"
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

func main() {
	// Listen on TCP port 2000 on all interfaces.
	l, err := net.Listen("tcp", ":80")
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

	//Sets the maximum size of a buffer for a demand
	const BUFFER_LENGTH = 1024
	var buf [BUFFER_LENGTH]byte

	n, err := c.Read(buf[0:])

	_, err2 := c.Write(buf[0:n])

	os.Stdout.Write(buf[0:n])
}

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