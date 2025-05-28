import socket
import threading

BROADCAST_PORT = 50000
BROADCAST_IP = '255.255.255.255'

def listen():
    sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    sock.bind(('', BROADCAST_PORT))
    print('Listening for UDP broadcasts...')
    while True:
        data, addr = sock.recvfrom(1024)
        print(f"Received from {addr}: {data.decode()}")

def send(message):
    sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    sock.setsockopt(socket.SOL_SOCKET, socket.SO_BROADCAST, 1)
    sock.sendto(message.encode(), (BROADCAST_IP, BROADCAST_PORT))
    sock.close()

if __name__ == '__main__':
    threading.Thread(target=listen, daemon=True).start()
    while True:
        msg = input('Enter message to broadcast: ')
        send(msg)