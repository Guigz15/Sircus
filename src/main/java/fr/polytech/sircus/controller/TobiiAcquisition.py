import tobiiresearch.implementation.EyeTracker
import time
import sys


def gaze_data_callback(gaze_data):
    # Print gaze points of left and right eye
    print("Left eye: ({gaze_left_eye}) \t Right eye: ({gaze_right_eye})".format(
        gaze_left_eye=gaze_data['left_gaze_point_on_display_area'],
        gaze_right_eye=gaze_data['right_gaze_point_on_display_area']))


if __name__ == "__main__":
    eyetracker = tobiiresearch.implementation.EyeTracker.find_all_eyetrackers()[0]

    eyetracker.subscribe_to(tobiiresearch.implementation.EyeTracker.EYETRACKER_GAZE_DATA, gaze_data_callback,
                            as_dictionary=True)
    time.sleep(float(sys.argv[1]))
    eyetracker.unsubscribe_from(tobiiresearch.implementation.EyeTracker.EYETRACKER_GAZE_DATA, gaze_data_callback)
