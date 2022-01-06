import 'package:flutter/material.dart';

class ProgressWidget extends StatefulWidget {
  const ProgressWidget({Key? key}) : super(key: key);

  @override
  State<ProgressWidget> createState() => _ProgressWidgetState();
}

class _ProgressWidgetState extends State<ProgressWidget>
    with TickerProviderStateMixin {
  late AnimationController _animationController;
  double _loadingPercentage = 0;

  @override
  void initState() {
    super.initState();

    _animationController = AnimationController(
      duration: const Duration(seconds: 3),
      vsync: this,
    )..addListener(() {
        setState(() {
          _loadingPercentage = _animationController.value;
        });
      });
    _animationController.forward();
  }

  @override
  Widget build(BuildContext context) {
    return Center(
      child: CircularProgressIndicator(
        value: _loadingPercentage,
      ),
    );
  }

  @override
  void dispose() {
    _animationController.dispose();
    super.dispose();
  }
}
