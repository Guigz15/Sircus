import tobiiresearch.implementation.EyeTracker
import time
from datetime import datetime
import sys


def gaze_data_callback(gaze_data):
    """
    Print gaze data and eye openness data.
    """
    print("Left eye: ({gaze_left_eye})\tLeft pupil diameter: {left_pupil_diameter}\t"
          "Right eye: ({gaze_right_eye})\tRight pupil diameter: {right_pupil_diameter}\tTime: {time_stamp}".format(
            gaze_left_eye=gaze_data['left_gaze_point_on_display_area'],
            left_pupil_diameter=gaze_data['left_pupil_diameter'],
            gaze_right_eye=gaze_data['right_gaze_point_on_display_area'],
            right_pupil_diameter=gaze_data['right_pupil_diameter'],
            time_stamp=datetime.now().strftime('%H:%M:%S.%f')[:-3]))


if __name__ == "__main__":
    eyetracker = tobiiresearch.implementation.EyeTracker.find_all_eyetrackers()[0]

    eyetracker.subscribe_to(tobiiresearch.implementation.EyeTracker.EYETRACKER_GAZE_DATA, gaze_data_callback,
                            as_dictionary=True)
    # Waiting time during acquisition
    time.sleep(float(sys.argv[1]) + 4.0)
    eyetracker.unsubscribe_from(tobiiresearch.implementation.EyeTracker.EYETRACKER_GAZE_DATA, gaze_data_callback)