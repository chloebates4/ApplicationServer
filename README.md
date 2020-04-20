# ApplicationServer

If you get this error while trying to run SimpleWebServer: 
```bash
IOExceptionAddress already in use
java.net.BindException: Address already in use
...
```
Fix with: 
Kill process Terminal: kill <pid>

Find pid: Terminal: lsof -i:<port>

