import tobiiresearch.implementation.EyeTracker
import time
from datetime import datetime
import sys


def gaze_data_callback(gaze_data):
    # Print gaze points of left and right eye
    print("Left eye: ({gaze_left_eye})\tRight eye: ({gaze_right_eye})\tTime: {time_stamp}".format(
        gaze_left_eye=gaze_data['left_gaze_point_on_display_area'],
        gaze_right_eye=gaze_data['right_gaze_point_on_display_area'],
        time_stamp=datetime.now().strftime('%H:%M:%S.%f')[:-3]))


if __name__ == "__main__":
    eyetracker = tobiiresearch.implementation.EyeTracker.find_all_eyetrackers()[0]

    eyetracker.subscribe_to(tobiiresearch.implementation.EyeTracker.EYETRACKER_GAZE_DATA, gaze_data_callback,
                            as_dictionary=True)
    time.sleep(float(sys.argv[1]))
    eyetracker.unsubscribe_from(tobiiresearch.implementation.EyeTracker.EYETRACKER_GAZE_DATA, gaze_data_callback)

