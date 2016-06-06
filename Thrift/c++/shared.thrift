/**
 * This thrift file can be included by other Thrift files that wants to share
 * these definitions.
 */
namespace cpp shared

struct SharedStruct {
	1: i32 key
	2: string value
}

service SharedService {
	SharedStruct getStruct(1: i32 key)
}
