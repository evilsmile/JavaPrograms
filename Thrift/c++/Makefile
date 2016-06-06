CC=g++

LIBSRC=$(shell ls gen-cpp/*cpp)
LIBOBJ=$(patsubst %.cpp,%.o,$(LIBSRC))
CPPFLAGS=-lthriftnb -levent -lthrift

.PHONY:init init-tutorial  init-shared lib clean

all:lib client server

init:init-tutorial  init-shared
	$(RM) -r gen-cpp/*skeleton.cpp

init-tutorial:tutorial.thrift 
	thrift -gen cpp $^

init-shared:shared.thrift
	thrift -gen cpp $^
	
lib:libcalculator.a

client:CppClient.cpp
	$(CC) -g -o client $^ libcalculator.a $(CPPFLAGS)

server:CppServer.cpp
	$(CC) -g -o server $^ libcalculator.a $(CPPFLAGS)

libcalculator.a:$(LIBOBJ)
	$(AR) crv $@ $^

clean:
	$(RM) -rf *.o gen-cpp libcalculator.a client server
