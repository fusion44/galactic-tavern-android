# simple server so save bandwidth during development
# serves files from tools directory.
# start with "python server.py" from command line

import http.server


def start_server(port=8000, bind=""):
    http.server.test(HandlerClass=http.server.SimpleHTTPRequestHandler, port=port, bind=bind)


start_server()
