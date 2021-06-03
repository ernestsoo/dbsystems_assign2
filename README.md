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
<br/>

`java treequery <INDEX FILE> <SDTNAME – LOWER BOUND> <SDTNAME – UPPER BOUND>`

eg.

`java treequery tree.128 "1001/01/2018 06:00:00 AM"`
<br/>

`java treequery tree-partial.128 "506/03/2012 10:00:00 AM"`
<br/>

`java treequery tree.128 "1001/01/2018 06:00:00 AM" "1001/01/2018 06:00:00 PM"`

<br/><br/>
**Important Note**: When creating a new index file with `treeload` on the aws instance, use the 'tree-partial.128' file when running the `treequery` (as mentioned in the report). To query on the full index file, use the 'tree.128' file.