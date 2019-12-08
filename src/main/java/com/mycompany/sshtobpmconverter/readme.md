From https://forums.nba-live.com/viewtopic.php?f=5&t=5547 | https://forum.xentax.com/viewtopic.php?t=1416&start=15

Guess: SSH is compressed FSH file (same file header)

You could try to 
1. replace SHPS to SHPI 
2. use FshEd/fshtool.exe to extract
3. Open the 0000.BIN with hex editor and delete data from "EAGL240 metal bin attachment for runtime" to end of file. Save.
4. Open this file with irfan view with raw options


SSH 

* Format Type : Image 
* Endian Order : Little Endian 



Format Specifications 

char {4} - Header (SHPS) 
uint32 {4} - File Length 
uint32 {4} - Nr of images within the file
char {4} - File Code (G359) 
char {4} - Filename (batt) 
uint32 {4} - Header Size? (48) 
char {4} - Group Name? (Buy ) 
char {4} - ERTS Header (ERTS) 
byte {16} - null 
uint32 {4} - Decompressed Image Size? 
uint16 {2} - Image Width/Height 
uint16 {2} - Image Width/Height 
uint32 {4} - null 
uint32 {4} - Unknown (8192) 

byte {X} - Image Data (up to fileLength-256) 

char {4} - EAGL Header (EAGL) 
char {236} - Description (240 metal bin attachment for runtime texture management) (null to fill) 
uint32 {4} - Unknown (112) 
char {4} - Filename (batt) 
uint64 {8} - null