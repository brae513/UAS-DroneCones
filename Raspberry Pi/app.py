from bluedot.btcomm import BluetoothServer
from signal import pause

reset = False
status = 1
connected = False

#This function processes the data recived from the cone
def data_received(data):
    global status, reset, s,connected
    if(data == "Connected"):
        connected=True
    if(data == "trigger"):
        status = 2
    if data == "reset":
        reset = True
        status = 1
    toSend = "1:" + str(status)
    print("data_received=Sending: "+str(toSend))
    s.send(toSend)
    print("Sent!")

#Send message to phone
def sendMsg(msg):
    global s
    print("sendMsg=Sending: "+str(msg))
    s.send(msg)
    print("Sent!")

#Set the status of a cone on the app
def setStatus(newStatus):
    global status,s,connected
    if(status!= newStatus):
        status = newStatus
        if connected:
            toSend = "1:" + str(status)
            s.send(toSend)


def getReset():
    global reset
    temp = reset
    reset = False
    return temp

def getStatus():
    return status

def startUp():
    return 0

#initialize server with the handler function
s=BluetoothServer(data_received)
