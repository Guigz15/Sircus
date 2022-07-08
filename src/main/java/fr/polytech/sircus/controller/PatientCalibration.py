import distutils.util
import random
import sys
import time
import tobiiresearch.implementation.EyeTracker
import tobiiresearch.implementation.ScreenBasedCalibration
from tkinter import *
import threading
from tobii_research_addons import ScreenBasedCalibrationValidation, Point2
from screeninfo import get_monitors

# Both monitors have to have the same resolution
screens = get_monitors()
secondScreen = None
for screen in screens:
    if not screen.is_primary:
        secondScreen = screen

root = Tk()
root.title('Calibration Patient')
root.geometry('%dx%d+%d+%d' % (secondScreen.width, secondScreen.height, secondScreen.x, 0))
root.overrideredirect(True)
root.bind("<Escape>", lambda event: root.overrideredirect(False))

canvas = Canvas(root, width=root.winfo_screenwidth(), height=root.winfo_screenheight(), borderwidth=0,
                highlightthickness=0, bg=str(sys.argv[3]))
canvas.pack()


def create_circle(x, y, r, **kwargs):
    return canvas.create_oval(x - r, y - r, x + r, y + r, **kwargs)


def calibrateAndValidate():
    eyetracker = tobiiresearch.implementation.EyeTracker.find_all_eyetrackers()[0]
    calibration = tobiiresearch.implementation.ScreenBasedCalibration.ScreenBasedCalibration(eyetracker)

    # Enter calibration mode.
    calibration.enter_calibration_mode()

    # Define the points on screen we should calibrate at.
    # The coordinates are normalized, i.e. (0.0, 0.0) is the upper left corner and (1.0, 1.0) is the lower right corner.
    points_to_calibrate = []
    targetNumbers = int(sys.argv[1])
    # targetNumbers = 5
    if targetNumbers == 2:
        points_to_calibrate = [(0.1, 0.1), (0.9, 0.9)]
    elif targetNumbers == 5:
        points_to_calibrate = [(0.5, 0.5), (0.1, 0.1), (0.1, 0.9), (0.9, 0.1), (0.9, 0.9)]
    elif targetNumbers == 9:
        points_to_calibrate = [(0.5, 0.5), (0.1, 0.1), (0.5, 0.1), (0.9, 0.1), (0.1, 0.5), (0.9, 0.5), (0.1, 0.9),
                               (0.5, 0.9), (0.9, 0.9)]

    randomizeTarget = bool(distutils.util.strtobool(sys.argv[2].capitalize()))
    if randomizeTarget:
        random.shuffle(points_to_calibrate)

    for point in points_to_calibrate:

        # Create target point with right color
        create_circle(x=root.winfo_screenwidth() * point[0], y=root.winfo_screenheight() * point[1], r=20,
                      fill=str(sys.argv[4]), outline=str(sys.argv[4]))
        create_circle(x=root.winfo_screenwidth() * point[0], y=root.winfo_screenheight() * point[1], r=5, fill='black',
                      outline='black')

        # Wait a little for user to focus.
        time.sleep(0.7)

        if calibration.collect_data(point[0], point[1]) \
                != tobiiresearch.implementation.ScreenBasedCalibration.CALIBRATION_STATUS_SUCCESS:
            # Try again if it didn't go well the first time.
            # Not all eye tracker models will fail at this point, but instead fail on ComputeAndApply.
            calibration.collect_data(point[0], point[1])

        # Erase each target point after calibration is done
        create_circle(x=root.winfo_screenwidth() * point[0], y=root.winfo_screenheight() * point[1], r=20,
                      fill=str(sys.argv[3]), outline=str(sys.argv[3]))
        create_circle(x=root.winfo_screenwidth() * point[0], y=root.winfo_screenheight() * point[1], r=5,
                      fill=str(sys.argv[3]), outline=str(sys.argv[3]))

    calibration_result = calibration.compute_and_apply()
    print("Compute and apply returned {0} and collected at {1} points.".
          format(calibration_result.status, len(calibration_result.calibration_points)))

    # The calibration is done. Leave calibration mode.
    calibration.leave_calibration_mode()

    points_to_validate = [Point2(0.3, 0.3), Point2(0.7, 0.3), Point2(0.3, 0.7), Point2(0.7, 0.7)]

    with ScreenBasedCalibrationValidation(eyetracker, timeout_ms=3000) as calib:
        for point in points_to_validate:
            # Create target point with right color
            create_circle(x=root.winfo_screenwidth() * point.x, y=root.winfo_screenheight() * point.y, r=20,
                          fill=str(sys.argv[4]), outline=str(sys.argv[4]))
            create_circle(x=root.winfo_screenwidth() * point.x, y=root.winfo_screenheight() * point.y, r=5,
                          fill='black', outline='black')

            calib.start_collecting_data(point)
            while calib.is_collecting_data:
                time.sleep(0.7)

            # Erase each target point after calibration is done
            create_circle(x=root.winfo_screenwidth() * point.x, y=root.winfo_screenheight() * point.y, r=20,
                          fill=str(sys.argv[3]), outline=str(sys.argv[3]))
            create_circle(x=root.winfo_screenwidth() * point.x, y=root.winfo_screenheight() * point.y, r=5,
                          fill=str(sys.argv[3]), outline=str(sys.argv[3]))

    calibration_result = calib.compute()

    print(calibration_result.average_accuracy_left)
    print(calibration_result.average_accuracy_right)
    print(calibration_result.average_precision_left)
    print(calibration_result.average_precision_right)
    print(calibration_result.average_precision_rms_left)
    print(calibration_result.average_precision_rms_right)

    root.quit()


root.after(1000, threading.Thread(target=calibrateAndValidate).start())
root.mainloop()
