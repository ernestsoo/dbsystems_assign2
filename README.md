## Commands on AWS instace:

<br/>

#### Navigate to project directory
`cd ~/BPlusTree/`

<br/>

#### Load index from heap
`java treeload <Heap Page Size>`

**eg.**

`java treeload 4096`

<br/>

#### Querying from index file

`java treequery <INDEX FILE> <SDTNAME>`
`java treequery <INDEX FILE> <SDTNAME – LOWER BOUND> <SDTNAME – UPPER BOUND>`

eg.

`java treequery tree.128 "1001/01/2018 06:00:00 AM"`
`java treequery tree.128 "1001/01/2018 06:00:00 AM" "1001/01/2018 06:00:00 PM"`