package com.johngoodstadt.freelancermanageimages

/*
delete file and then rename others to keep sequence
 */
fun removeFileAndRenameDown(UID:String, start: Int, end: Int) {

    val filename = LibraryFilesystem.getFileNameByUID(UID, start.toString())
    LibraryFilesystem.removeFile(filename)

    val startNumber = start + 1
    for (x in startNumber..end) {
        val newNumber = x - 1

        LibraryFilesystem.renameFile(UID, x.toString(), newNumber.toString())
    }
}
/*
rename files to make a space for a new file - keep sequence numbers
 */
fun renameFilesUp(UID:String, start: Int, end: Int) {

    for (x in end downTo start) {
        val newNumber = x + 1

        LibraryFilesystem.renameFile(UID, x.toString(), newNumber.toString())
    }
}