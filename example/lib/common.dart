import 'dart:math' as math;

/// Format bytes
String formatBytes(int bytes, [int unit = 1024, int precision = 1]) {
  if (bytes != 0) {
    final double base = math.log(bytes) / math.log(unit);
    final suffix = const ['B', 'KB', 'MB', 'GB', 'TB'][base.floor()];
    final size = math.pow(unit, base - base.floor());
    return '${size.toStringAsFixed(precision)}$suffix';
  } else {
    return "0B";
  }
}
