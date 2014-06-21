package main

import (
	"os"
	"io"
	"log"
	"net"
)

func main() {
	// Listen on TCP port 2000 on all interfaces.
	l, err := net.Listen("tcp", ":8080")
	if err != nil {
		log.Fatal(err)
	}
	defer l.Close()
	for {
		// Wait for a connection.
		conn, err := l.Accept()
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

	_, _ := c.Write(buf[0:n])

	os.Stout.Write(buf)
}