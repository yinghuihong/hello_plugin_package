import 'dart:typed_data';

import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;

//
class LoadImageScreen extends StatefulWidget {
  const LoadImageScreen({Key? key}) : super(key: key);

  @override
  State<LoadImageScreen> createState() => _LoadImageScreenState();
}

class _LoadImageScreenState extends State<LoadImageScreen> {
  Uint8List? bytes;

  @override
  void initState() {
    super.initState();
    Future.delayed(const Duration(milliseconds: 500), () {
      fetchAlbum();
    });
  }

  @override
  Widget build(BuildContext context) {
    debugPrint("xxx build");
    return Scaffold(
      appBar: AppBar(
        title: const Text('Load Image'),
      ),
      body: Center(
        child: bytes == null
            ? const CircularProgressIndicator()
            : Image.memory(bytes!),
      ),
    );
  }

  void fetchAlbum() {
    http
        .get(Uri.parse(
            'http://speed1.redtone.com:8080/speedtest/random1000x1000.jpg'))
        .then((value) {
      bytes = value.bodyBytes;
      setState(() {});
    });
  }
}
