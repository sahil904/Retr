import 'package:flutter/material.dart';
import './ui/Home.dart';
void main(){
  var title="Gesture";
  runApp(MaterialApp(
    title: title,
    home: new Home(title:title,) ,
  ));
}