import os
import subprocess
import glob
import tobiiresearch.implementation.EyeTracker

try:
    ETM_PATH = glob.glob(os.environ["LocalAppData"] + "\Programs\TobiiProEyeTrackerManager\TobiiProEyeTrackerManager.exe")[0]

    eyetracker = tobiiresearch.implementation.EyeTracker.find_all_eyetrackers()[0]

    etm_p = subprocess.Popen([ETM_PATH, "--device-address=" + eyetracker.address],
                             stdout=subprocess.PIPE, stderr=subprocess.PIPE, shell=False)

except Exception as e:
    print(e)


