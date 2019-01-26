import 'package:flutter/material.dart';
class Second extends StatelessWidget{
  @override
  Widget build(BuildContext context) {
    // TODO: implement build
    return new Scaffold(
      backgroundColor: Colors.amber,
      appBar: new AppBar(
        backgroundColor: Colors.deepPurpleAccent,
        title: new Text("First"),
      actions: <Widget>[new IconButton(icon: new Icon(Icons.keyboard_backspace),
          onPressed:()=>debugPrint("icon tapped")),
      new IconButton(icon: new Icon(Icons.search), onPressed: null)
      ],
      ),
    body: new Container(
      alignment: Alignment.center,
      child: new Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: <Widget>[
          new Text("hi Sahil",style: new TextStyle(fontSize: 20,
              color: Colors.deepPurpleAccent,
              fontWeight: FontWeight.w300),),
          new InkWell(child: new Text("Button"),)
        ],
      ),
    ),
    floatingActionButton: new FloatingActionButton(onPressed: ()=>debugPrint("sfds?"),
      backgroundColor: Colors.deepPurpleAccent,
      child: new Icon(Icons.local_airport),),

    bottomNavigationBar: new BottomNavigationBar(items: [
      new BottomNavigationBarItem(icon: new Icon(Icons.access_alarm),title: new Text("Alaram")
      ),
      new BottomNavigationBarItem(icon: new Icon(Icons.keyboard_backspace),title: new Text("Back")),
      new BottomNavigationBarItem(icon: new Icon(Icons.local_airport),title: new Text("Airplane"))
    ],onTap: (int i)=>debugPrint("Hey sahil $i"),),
    );

  }
}