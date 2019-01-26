import 'package:flutter/material.dart';
class Home extends StatelessWidget{
   String title;


  Home({Key key,this.title}):super(key:key );

  @override
  Widget build(BuildContext context) {
    return new Scaffold(appBar: new AppBar(
      title: new Text(title),

    ),
      body: new Center(
        child: new cutomButton(),
    )
    ,

    );
  }
}

class cutomButton extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    // TODO: implement build
    return new GestureDetector(
      onTap: (){
        final snackBar=new SnackBar(content: new Text("Hello Gesture"),
          backgroundColor: Theme.of(context).backgroundColor,duration: new Duration(
            hours: 0,minutes: 0,seconds: 0,milliseconds: 3000,microseconds: 0
          ),);
        Scaffold.of(context).showSnackBar(snackBar);
      },
        child: new Container(
          padding: new EdgeInsets.all(18.0),
          decoration: new BoxDecoration(
            color:  Theme.of(context).accentColor,
            borderRadius: new BorderRadius.circular(5.5)
          ),
          child: new Text("first button"),
        ),
    );
  }
}