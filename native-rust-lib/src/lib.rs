
#[no_mangle]
pub extern "C" fn add_numbers(a: i32, b: i32) -> i32 {
    a + b
}

#[no_mangle]
pub extern "C" fn create_int_pointer(value: i32) -> *mut i32 {
    let boxed = Box::new(value); // Allocate the value on the heap
    Box::into_raw(boxed)        // Return a raw pointer
}

#[no_mangle]
pub extern "C" fn free_int_pointer(ptr: *mut i32) {
    if !ptr.is_null() {
        unsafe {
            Box::from_raw(ptr); // Reclaim ownership to drop the memory
        }
    }
}