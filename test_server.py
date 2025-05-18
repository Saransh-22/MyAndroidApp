from flask import Flask
from flask_cors import CORS

app = Flask(__name__)
CORS(app)

@app.route('/')
def hello():
    return "Hello, World! Server is running!"

@app.route('/test')
def test():
    return {"message": "Test endpoint working!"}

if __name__ == '__main__':
    print("Starting test server...")
    print("Try these URLs in your browser:")
    print("http://127.0.0.1:5000/")
    print("http://127.0.0.1:5000/test")
    app.run(debug=True, host='0.0.0.0', port=5000) 