echo "Deploying to Android Devices."

for SERIAL in $(adb devices | tail -n +2 | cut -sf 1);
do
  for APKLIST in $(ls Application/build/outputs/apk/*.apk);
  do
  echo "Installatroning $APKLIST on $SERIAL"
  # adb -s $SERIAL -d install -r $APKLIST
  # adb shell am start -n computer.camp.clay.designer/camp.computer.clay.application.Launcher
  adb -s $SERIAL shell am start -n computer.camp.clay.designer/camp.computer.clay.application.Launcher
  done

  # for MP4LIST in $(ls *.mp4);
  # do
  # echo "Installatroning $MP4LIST to $SERIAL"
  # adb -s $SERIAL push $MP4LIST sdcard/
  # done
done

echo "Done."
