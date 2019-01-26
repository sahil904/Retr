import 'package:flutter/material.dart';
class Home extends StatelessWidget{
  @override
  Widget build(BuildContext context) {
    // TODO: implement build
    return new Container(
      alignment: Alignment.center,
      color: Colors.amberAccent,


  child: new Column(
    mainAxisAlignment: MainAxisAlignment.center,
 children: <Widget>[new Text("item 1",textDirection: TextDirection.ltr,
   style: TextStyle(color: Colors.blue,fontSize: 20),),
 new Text("item 2",textDirection: TextDirection.ltr,),
  const Expanded(child: const Text("item 3"))
   // new Text("Item 3",textDirection: TextDirection.ltr,)
 ],
  )
  /*    child: new Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: <Widget>[ new Text("This a First Text",
          textDirection: TextDirection.ltr,
          style: TextStyle(color: Colors.black,fontSize: 30),),
       new Text("This is a Second Text",
         textDirection: TextDirection.ltr,
         style: new TextStyle(color: Colors.deepOrange,fontSize: 25),),


new Container(alignment: Alignment.centerLeft,
child: new Text("this is a Third Text",style: new TextStyle(fontSize: 10,color: Colors.blue)),
)
        ],
      )*/,
      //child: new Text("hello there any one",textDirection: TextDirection.ltr,
      //style: new TextStyle(color: Colors.deepOrangeAccent,fontWeight: FontWeight.w900,fontSize: 20),
    //  ),
    );
  }
}