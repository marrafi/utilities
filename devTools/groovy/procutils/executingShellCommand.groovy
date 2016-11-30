//specifying the command
def process=new ProcessBuilder("pwd")
//specifying the workingDirectory
                                    .directory(new File("/bin"))
//printing error stream on the output
									.redirectErrorStream(true) 
                                    .start()
  process.inputStream.eachLine {println it}
// wwaiting for the process to complete it's execution
  process.waitFor();