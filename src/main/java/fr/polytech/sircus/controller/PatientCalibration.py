import distutils.util
import os
import random
import sys
import threading
import time
import tobiiresearch.implementation.EyeTracker
import tobiiresearch.implementation.ScreenBasedCalibration
from tkinter import *
from tobii_research_addons import ScreenBasedCalibrationValidation, Point2
from screeninfo import get_monitors

test = True

# Both monitors have to have the same resolution
if not test:
    screens = get_monitors()
    secondScreen = None
    for screen in screens:
        if not screen.is_primary:
            secondScreen = screen

# Create the window to display calibration and validation points
root = Tk()
root.title('Calibration Participant')
# To force the window to be on the second screen
if not test:
    root.geometry('%dx%d+%d+%d' % (secondScreen.width, secondScreen.height, secondScreen.x, 0))
root.overrideredirect(True)
root.bind("<Escape>", lambda event: close())

if not test:
    # Set the canvas properties, especially background color choose by the user
    canvas = Canvas(root, width=root.winfo_screenwidth(), height=root.winfo_screenheight(), borderwidth=0,
                    highlightthickness=0, bg=str(sys.argv[3]))
else:
    canvas = Canvas(root, width=root.winfo_screenwidth(), height=root.winfo_screenheight(), borderwidth=0,
                    highlightthickness=0, bg='white')
canvas.pack()


def restart_program():
    """
    Restarts the current program.
    """
    python = sys.executable
    os.execl(python, python, * sys.argv)


def close():
    """
    Triggered when escape is pressed to close the window.
    """
    root.quit()
    sys.exit()


def create_circle(x, y, r, **kwargs):
    """
    Create circle with only center coordinates and radius.
    :param x: x coordinate of the center
    :param y: y coordinate of the center
    :param r: radius of the circle
    """
    return canvas.create_oval(x - r, y - r, x + r, y + r, **kwargs)


def plot(calibration_result, validation_result):
    """
    Display the calibration plot.
    :param calibration_result: data from calibration
    :param validation_result: data from validation
    """
    plot_window = Toplevel(root)
    plot_window.attributes("-fullscreen", True)

    # Calibration plot canvas
    plot_canvas = Canvas(plot_window, width=3*plot_window.winfo_screenwidth()/4, height=plot_window.winfo_screenheight(),
                         borderwidth=0, highlightthickness=0, bg='white')
    plot_canvas.pack(anchor=W, side=LEFT)

    # Result labels
    average_accuracy_left = Label(plot_window, text="Précision moyenne gauche : " + str(validation_result.average_accuracy_left), font=("Arial", 10))
    average_accuracy_left.pack(anchor=E, side=TOP, fill=X, pady=20)
    average_accuracy_right = Label(plot_window, text="Précision moyenne droite : " + str(validation_result.average_accuracy_right), font=("Arial", 10))
    average_accuracy_right.pack(anchor=E, side=TOP, fill=X, pady=20)
    average_precision_left = Label(plot_window, text="Précision moyenne gauche (deg) : " + str(validation_result.average_precision_left), font=("Arial", 10))
    average_precision_left.pack(anchor=E, side=TOP, fill=X, pady=20)
    average_precision_right = Label(plot_window, text="Précision moyenne droite (deg) : " + str(validation_result.average_precision_right), font=("Arial", 10))
    average_precision_right.pack(anchor=E, side=TOP, fill=X, pady=20)
    average_precision_rms_left = Label(plot_window, text="Précision quadratique moyenne gauche : " + str(validation_result.average_precision_rms_left), font=("Arial", 10))
    average_precision_rms_left.pack(anchor=E, side=TOP, fill=X, pady=20)
    average_precision_rms_right = Label(plot_window, text="Précision quadratique moyenne droite : " + str(validation_result.average_precision_rms_right), font=("Arial", 10))
    average_precision_rms_right.pack(anchor=E, side=TOP, fill=X, pady=20)

    # Finish button
    finish_button = Button(plot_window, text="Terminer", command=close, bg="#FC8571")
    finish_button.pack(anchor=E, side=BOTTOM, pady=50, padx=plot_window.winfo_screenwidth()/9.5)

    # Relaunch calibration button
    relaunch_button = Button(plot_window, text="Relancer la calibration", bg="#98D7E6", command=restart_program)
    relaunch_button.pack(anchor=E, side=BOTTOM, padx=plot_window.winfo_screenwidth()/12)

    def create_circle_for_plot(x, y, r, **kwargs):
        """
        Create circle with only center coordinates and radius.
        :param x: x coordinate of the center
        :param y: y coordinate of the center
        :param r: radius of the circle
        """
        return plot_canvas.create_oval(x - r, y - r, x + r, y + r, **kwargs)

    if not test:
        for calibration_point in calibration_result.calibration_points:
            position = calibration_point.position_on_display_area
            create_circle_for_plot(x=3*plot_window.winfo_screenwidth()/4 * position[0],
                                   y=plot_window.winfo_screenheight() * position[1], r=20)
            create_circle_for_plot(x=3*plot_window.winfo_screenwidth()/4 * position[0],
                                   y=plot_window.winfo_screenheight() * position[1], r=5, fill='black')
            for calibration_sample in calibration_point.calibration_samples:
                left_eye_position = calibration_sample.left_eye.position_on_display_area
                right_eye_position = calibration_sample.right_eye.position_on_display_area
                plot_canvas.create_line(3*plot_window.winfo_screenwidth()/4 * position[0],
                                        plot_window.winfo_screenheight() * position[1],
                                        3*plot_window.winfo_screenwidth()/4 * left_eye_position[0],
                                        plot_window.winfo_screenheight() * left_eye_position[1],
                                        fill='red', width=1.5)
                plot_canvas.create_line(3*plot_window.winfo_screenwidth()/4 * position[0],
                                        plot_window.winfo_screenheight() * position[1],
                                        3*plot_window.winfo_screenwidth()/4 * right_eye_position[0],
                                        plot_window.winfo_screenheight() * right_eye_position[1],
                                        fill='green', width=1.5)

        for validation_point in validation_result.points:
            create_circle_for_plot(x=plot_window.winfo_screenwidth() * validation_point.x,
                                   y=plot_window.winfo_screenheight() * validation_point.y,
                                   r=20)
            create_circle_for_plot(x=plot_window.winfo_screenwidth() * validation_point.x,
                                   y=plot_window.winfo_screenheight() * validation_point.y,
                                   r=5, fill='black')
            for gaze_data in validation_result.points[validation_point][0].gaze_data:
                left_eye_position = gaze_data.left_eye.gaze_point.position_on_display_area
                right_eye_position = gaze_data.right_eye.gaze_point.position_on_display_area
                plot_canvas.create_line(3*plot_window.winfo_screenwidth()/4 * validation_point.x,
                                        plot_window.winfo_screenheight() * validation_point.y,
                                        3*plot_window.winfo_screenwidth()/4 * left_eye_position[0],
                                        plot_window.winfo_screenheight() * left_eye_position[1],
                                        fill='red', width=1.5)
                plot_canvas.create_line(3*plot_window.winfo_screenwidth()/4 * validation_point.x,
                                        plot_window.winfo_screenheight() * validation_point.y,
                                        3*plot_window.winfo_screenwidth()/4 * right_eye_position[0],
                                        plot_window.winfo_screenheight() * right_eye_position[1],
                                        fill='green', width=1.5)
    else:
        points_to_calibrate = [(0.5, 0.5), (0.1, 0.1), (0.5, 0.1), (0.9, 0.1), (0.1, 0.5), (0.9, 0.5), (0.1, 0.9),
                               (0.5, 0.9), (0.9, 0.9)]
        for position in points_to_calibrate:
            create_circle_for_plot(x=3*plot_window.winfo_screenwidth()/4 * position[0],
                                   y=plot_window.winfo_screenheight() * position[1], r=20)
            create_circle_for_plot(x=3*plot_window.winfo_screenwidth()/4 * position[0],
                                   y=plot_window.winfo_screenheight() * position[1], r=5, fill='black')
            for calibration_sample in calibration_result:
                plot_canvas.create_line(3*plot_window.winfo_screenwidth()/4 * position[0],
                                        plot_window.winfo_screenheight() * position[1],
                                        3*plot_window.winfo_screenwidth()/4 * calibration_sample[0],
                                        plot_window.winfo_screenheight() * calibration_sample[1],
                                        fill='red', width=1.5)

        points_to_validate = [Point2(0.7, 0.7), Point2(0.3, 0.3), Point2(0.7, 0.3), Point2(0.3, 0.7)]
        for validation_point in points_to_validate:
            create_circle_for_plot(x=3*plot_window.winfo_screenwidth()/4 * validation_point.x,
                                   y=plot_window.winfo_screenheight() * validation_point.y,
                                   r=20)
            create_circle_for_plot(x=3*plot_window.winfo_screenwidth()/4 * validation_point.x,
                                   y=plot_window.winfo_screenheight() * validation_point.y,
                                   r=5, fill='black')
            for gaze_data in validation_result:
                plot_canvas.create_line(3*plot_window.winfo_screenwidth()/4 * validation_point.x,
                                        plot_window.winfo_screenheight() * validation_point.y,
                                        3*plot_window.winfo_screenwidth()/4 * gaze_data.x,
                                        plot_window.winfo_screenheight() * gaze_data.y,
                                        fill='red', width=1.5)


def targetCalibrationValidation(eyetracker, calibration, points_to_calibrate, points_to_validate):
    """
    Perform calibration and validation when we have point as target
    :return: calibration and validation results
    """
    # Enter calibration mode.
    calibration.enter_calibration_mode()

    first_point = True
    big_circle = None
    little_circle = None

    for point in points_to_calibrate:
        # Create target point with right color
        if first_point:
            if not test:
                targetColor = sys.argv[4][sys.argv[4].find(':') + 1:]
                big_circle = create_circle(x=root.winfo_screenwidth() * point[0],
                                           y=root.winfo_screenheight() * point[1], r=20,
                                           fill=targetColor, outline=targetColor)
            else:
                big_circle = create_circle(x=root.winfo_screenwidth() * point[0],
                                           y=root.winfo_screenheight() * point[1], r=20,
                                           fill='black', outline='black')
            little_circle = create_circle(x=root.winfo_screenwidth() * point[0],
                                          y=root.winfo_screenheight() * point[1], r=5,
                                          fill='black', outline='black')
            first_point = False

        # Subtraction between big_circle current coordinates and point coordinate to know which direction taken
        width_diff = (root.winfo_screenwidth() * point[0] - 20) - round(canvas.coords(big_circle).__getitem__(0), 2)
        height_diff = (root.winfo_screenheight() * point[1] - 20) - round(canvas.coords(big_circle).__getitem__(1), 2)
        speed = 300
        if height_diff < 1000 and width_diff < 1000:
            speed = 200

        while round(canvas.coords(big_circle).__getitem__(0), 0) != (root.winfo_screenwidth() * point[0] - 20) \
                or round(canvas.coords(big_circle).__getitem__(1), 0) != (root.winfo_screenheight() * point[1] - 20):
            canvas.move(big_circle, width_diff / speed, height_diff / speed)
            canvas.move(little_circle, width_diff / speed, height_diff / speed)
            time.sleep(0.001)
            canvas.update()

        # Wait a little for user to focus.
        time.sleep(0.7)

        if calibration.collect_data(point[0], point[1]) \
                != tobiiresearch.implementation.ScreenBasedCalibration.CALIBRATION_STATUS_SUCCESS:
            # Try again if it didn't go well the first time.
            # Not all eye tracker models will fail at this point, but instead fail on ComputeAndApply.
            calibration.collect_data(point[0], point[1])

    calibration_result = calibration.compute_and_apply()
    print("Compute and apply returned {0} and collected at {1} points.".
          format(calibration_result.status, len(calibration_result.calibration_points)))

    # The calibration is done. Leave calibration mode.
    calibration.leave_calibration_mode()

    with ScreenBasedCalibrationValidation(eyetracker, timeout_ms=3000, sample_count=30) as calib:
        for point in points_to_validate:
            # Subtraction between big_circle current coordinates and point coordinate to know which direction taken
            width_diff = (root.winfo_screenwidth() * point.x - 20) - round(canvas.coords(big_circle).__getitem__(0), 2)
            height_diff = (root.winfo_screenheight() * point.y - 20) - round(canvas.coords(big_circle).__getitem__(1), 2)
            speed = 300
            if height_diff < 1000 and width_diff < 1000:
                speed = 200

            while round(canvas.coords(big_circle).__getitem__(0), 0) != (root.winfo_screenwidth() * point.x - 20) \
                    or round(canvas.coords(big_circle).__getitem__(1), 0) != (root.winfo_screenheight() * point.y - 20):
                canvas.move(big_circle, width_diff / speed, height_diff / speed)
                canvas.move(little_circle, width_diff / speed, height_diff / speed)
                time.sleep(0.001)
                canvas.update()

            calib.start_collecting_data(point)
            while calib.is_collecting_data:
                time.sleep(0.7)

    validation_result = calib.compute()

    # Erase target point at the end
    if not test:
        create_circle(x=round(canvas.coords(big_circle).__getitem__(0), 1) + 20,
                      y=round(canvas.coords(big_circle).__getitem__(1), 1) + 20,
                      r=20, fill=str(sys.argv[3]), outline=str(sys.argv[3]))
        create_circle(x=round(canvas.coords(little_circle).__getitem__(0), 1) + 5,
                      y=round(canvas.coords(little_circle).__getitem__(0), 1) + 5,
                      r=5, fill=str(sys.argv[3]), outline=str(sys.argv[3]))
    else:
        create_circle(x=round(canvas.coords(big_circle).__getitem__(0), 1) + 20,
                      y=round(canvas.coords(big_circle).__getitem__(1), 1) + 20,
                      r=20, fill='white', outline='white')
        create_circle(x=round(canvas.coords(little_circle).__getitem__(0), 1) + 5,
                      y=round(canvas.coords(little_circle).__getitem__(0), 1) + 5,
                      r=5, fill='white', outline='white')

    return calibration_result, validation_result


def calibrateAndValidate():
    """
    Perform the calibration and the validation.
    """
    eyetracker = tobiiresearch.implementation.EyeTracker.find_all_eyetrackers()[0]
    calibration = tobiiresearch.implementation.ScreenBasedCalibration.ScreenBasedCalibration(eyetracker)

    # Define the points on screen we should calibrate at.
    # The coordinates are normalized, i.e. (0.0, 0.0) is the upper left corner and (1.0, 1.0) is the lower right corner.
    points_to_calibrate = []
    if not test:
        targetNumbers = int(sys.argv[1])
    else:
        targetNumbers = 9
    if targetNumbers == 2:
        points_to_calibrate = [(0.9, 0.9), (0.1, 0.1)]
    elif targetNumbers == 5:
        points_to_calibrate = [(0.5, 0.5), (0.1, 0.1), (0.1, 0.9), (0.9, 0.1), (0.9, 0.9)]
    elif targetNumbers == 9:
        points_to_calibrate = [(0.5, 0.5), (0.1, 0.1), (0.5, 0.1), (0.9, 0.1), (0.1, 0.5), (0.9, 0.5), (0.1, 0.9),
                               (0.5, 0.9), (0.9, 0.9)]

    if not test:
        randomizeTarget = bool(distutils.util.strtobool(sys.argv[2].capitalize()))
    else:
        randomizeTarget = True
    if randomizeTarget:
        random.shuffle(points_to_calibrate)

    points_to_validate = [Point2(0.7, 0.7), Point2(0.3, 0.3), Point2(0.7, 0.3), Point2(0.3, 0.7)]
    if randomizeTarget:
        random.shuffle(points_to_validate)

    calibration_result = None
    validation_result = None
    if not test:
        if sys.argv[4][:sys.argv[4].find(':')] == "target":
            calibration_result, validation_result = targetCalibrationValidation(eyetracker, calibration,
                                                                                points_to_calibrate, points_to_validate)
        '''elif sys.argv[4][:sys.argv[4].find(':')] == "image":
            calibration_result, validation_result = imageCalibrationValidation(eyetracker, calibration, points_to_calibrate,
                                                                               points_to_validate)
        elif sys.argv[4][:sys.argv[4].find(':')] == "video":
        calibration_result, validation_result = videoCalibrationValidation(eyetracker, calibration, points_to_calibrate,
                                                                       points_to_validate)'''
    else:
        calibration_result = [(0.486258, 0.525314), (0.12143, 0.111205), (0.4852, 0.105423), (0.9125646, 0.132154),
                              (0.095112, 0.50235), (0.9235, 0.485), (0.1235, 0.9005544), (0.545155, 0.982666), (0.9124, 0.8872)]

        validation_result = [Point2(0.68942, 0.71240), Point2(0.32145, 0.30214), Point2(0.67892, 0.3124), Point2(0.314268, 0.775612)]

    print(validation_result.average_accuracy_left)
    print(validation_result.average_accuracy_right)
    print(validation_result.average_precision_left)
    print(validation_result.average_precision_right)
    print(validation_result.average_precision_rms_left)
    print(validation_result.average_precision_rms_right)

    plot(calibration_result, validation_result)


root.after(1000, threading.Thread(target=calibrateAndValidate).start())
root.mainloop()
