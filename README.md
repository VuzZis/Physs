# Physs
![Physs banner](/img/physs_banner.png)
## Installation
* Download latest .zip file from **Releases**
* Unpack the zip file in some folder you want to. *(e.g. Programs folder)*
* Run the `path.bat` file if you want to access it in command line just by name. If not (`.\physs.cmd`)
* Go and write some scripts! :D

## Information
### What is Physs?
Physs is a custom interpreted language, with a similar syntax to JavaScript.
The language is interpreted on Java, so don't consider it a serious language.

### What can/will it do?
It's a good solution if you want to have custom interpreted scripts in your Java Project.
* Annotations support (Custom and Java)
* OOP support
* Imports from Java packages (e.g. importJava("com.skoow.physs.Physs");)
* Default language features like vars, loops, literals and etc.

### Is this free to copy, edit, etc.?
> Yes, but you should put credits on it.

### What is it syntax?
```javascript
val a = in "Please enter a: ";
val b = in "Please enter b: ";
if(a == b) out "Those are equal!";
else if (a+b == b or a+b == a) {
   a = a+b;
} 
else {
   a = a+b;
   b = b+a;
}
val i = 10; 
while(i > 0) {
    i = i -1;
}
out a+b;
```
