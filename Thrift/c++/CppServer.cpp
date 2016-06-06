#include "gen-cpp/Calculator.h"

#include <thrift/protocol/TBinaryProtocol.h>
#include <thrift/server/TSimpleServer.h>
#include <thrift/server/TThreadPoolServer.h>
#include <thrift/server/TThreadedServer.h>
#include <thrift/transport/TServerSocket.h>
#include <thrift/transport/TSocket.h>
#include <thrift/transport/TTransportUtils.h>
#include <thrift/TToString.h>
#include <thrift/concurrency/ThreadManager.h>
#include <thrift/concurrency/PlatformThreadFactory.h>

#include <boost/make_shared.hpp>

#include <iostream>
#include <stdexcept>
#include <sstream>

using namespace std;
using namespace ::apache::thrift;
using namespace ::apache::thrift::protocol;
using namespace ::apache::thrift::concurrency;
using namespace ::apache::thrift::transport;
using namespace ::apache::thrift::server;

using boost::shared_ptr;

using namespace  ::tutorial;
using namespace  ::shared;

class CalculatorHandler : virtual public CalculatorIf {
 public:
  CalculatorHandler() {
  }

  /**
   * A method definition looks like C code. It has a return type, arguments,
   * and optionally a list of exceptions that it may throw. Note that argument
   * lists and exception lists are specified using the exact same syntax as
   * fields lists in struct or exception definition.
   */
  void ping() {
    cout << "S_ping()" << endl;
  }

  int32_t add(const int32_t num1, const int32_t num2) {
    cout << "add(" << num1 << ", " << num2 << ")" << endl;
	return num1 + num2;
  }

  int32_t calculate(const int32_t logid, const Work& work) {
	cout << "calculate(" << logid << ", " << work << ")" << endl;
	int32_t val;

	switch(work.op) {
		case Operation::ADD:
			val = work.num1 + work.num2;
			break;
		case Operation::SUBTRACT:
			val = work.num1 - work.num2;
			break;
		case Operation::MULTIPLY:
			val = work.num1 * work.num2;
			break;
		case Operation::DIVIDE:
			if (work.num2 == 0) {
				InvalidOperation io;
				io.whatOp = work.op;
				io.why = "Cannot divide by 0";
				throw io;
			}
			val = work.num1 / work.num2;
			break;
		default:
			InvalidOperation io;
			io.whatOp = work.op;
			io.why = "Invalid operation";
			throw io;
		}
	
	SharedStruct ss;
	ss.key = logid;
	ss.value = to_string(val);

	log[logid] = ss;
	return val;
  }

  void getStruct(SharedStruct& ret, const int32_t logid) {
	cout << "getStruct(" << logid << ")" <<endl;
	ret = log[logid];
  }

  void zip() {
    cout << "zip() " << endl;
  }

protected:
  map<int32_t, SharedStruct> log;

};

int main(int argc, char **argv) {
  int port = 9090;
  shared_ptr<CalculatorHandler> handler(new CalculatorHandler());
  shared_ptr<TProcessor> processor(new CalculatorProcessor(handler));
  shared_ptr<TServerTransport> serverTransport(new TServerSocket(port));
  shared_ptr<TTransportFactory> transportFactory(new TBufferedTransportFactory());
  shared_ptr<TProtocolFactory> protocolFactory(new TBinaryProtocolFactory());

  TSimpleServer server(processor, serverTransport, transportFactory, protocolFactory);
  server.serve();
  return 0;
}

