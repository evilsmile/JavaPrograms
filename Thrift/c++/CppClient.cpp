#include <iostream>

#include <thrift/protocol/TBinaryProtocol.h>
#include <thrift/transport/TSocket.h>
#include <thrift/transport/TTransportUtils.h>

#include "gen-cpp/Calculator.h"

using namespace std;
using namespace apache::thrift;
using namespace apache::thrift::protocol;
using namespace apache::thrift::transport;

using namespace ::tutorial;
using namespace ::shared;

int main() {
	boost::shared_ptr<TTransport> socket(new TSocket("localhost", 9090));
	boost::shared_ptr<TTransport> transport(new TBufferedTransport(socket));
	boost::shared_ptr<TProtocol> protocol(new TBinaryProtocol(transport));
	CalculatorClient client(protocol);

	try {
		transport->open();
	
		client.ping();
		cout << "ping()" << endl;

		cout << "1 + 1 = " << client.add(1, 1) << endl;

		Work work;
		work.op = Operation::DIVIDE;
		work.num1 = 1;
		work.num2 = 0;

		try {
			client.calculate(1, work);
			cout << "Whoa? We can divide by zero!" << endl;
		} catch (InvalidOperation &io) {
			cout << "InvalidOperation: " << io.why << endl;
		}

		work.op = Operation::SUBTRACT;
		work.num1 = 15;
		work.num2 = 10;
		int32_t diff = client.calculate(1, work);
		cout << "15 - 10 =" << diff << endl;

		SharedStruct ss;
		client.getStruct(ss, 1);
		cout << "Recveived log: " << ss << endl;

		transport->close();
	} catch (TException & tx) {
		cout << "ERROR: " << tx.what() << endl;
	}
}

