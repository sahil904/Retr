import 'package:flutter/material.dart';
import 'dart:async';
import 'dart:convert';
import 'package:http/http.dart' as http;


void main() async {
  List _data=await getJson();
  for(int i=0;i<_data.length;i++){
    print(_data[i]["title"]);
  }
  String body=_data[2]['title'];
runApp(new MaterialApp(
  home: new Scaffold(
    appBar: new AppBar(
      title: new Text("json parsing"),
      centerTitle: true,
      backgroundColor: Colors.deepPurpleAccent,
    ),
    body: new Center(
      child: new ListView.builder(
          itemCount: _data.length,
          padding: const EdgeInsets.all(16),

          itemBuilder: (BuildContext context,int position) {
            if(position.isOdd)
              return new Divider();

            return new ListTile(

             leading: new Text(_data[position]['title'],),



              onTap: (){ShowOnTapMessage(context,"${_data[position]["title"]}");},

            );
            }

      )),
  ),
));
  }
  void ShowOnTapMessage(BuildContext context,String message){
  var alert=new AlertDialog(
    title: new Text("Alert"),
    content: new Text(message),
    actions: <Widget>[new FlatButton(onPressed: (){Navigator.pop(context);}, child: new Text("ok"))],
  );
  showDialog(context: context,child: alert);
  }
Future<List> getJson() async{
  String apiUrl="https://jsonplaceholder.typicode.com/photos";
  http.Response response=await http.get(apiUrl);
  return  json.decode(response.body);
}