import React, { Component } from 'react'
import {Container,
  Button,
  Row,
  Col,
  Form,
  FormControl
} from "react-bootstrap";
import './Login.css';

const BASE_URL = 'http://localhost:8082';

class Login extends Component{ 
    constructor(props){
        super(props);
        this.state ={
          password: "",
            username: "",
            loginError: "",
              errors: {
                password: "",
                username: ""
          }
        };
    }

    validateInputs = (username, password) => {
      const errors = {};
    
    if(!username){
      errors.username = "Username should not be empty."
    } else if(username.length <3 || username.length > 30){
      errors.username = "Username must be between 3 and 30 characters."
    }

    if(!password){
      errors.password = "Password should not be empty."
    } else if(password.length <4 || password.length > 30){
      errors.password = "Password must be between 4 and 30 characters."
    }
return errors;
    };
onChange = e =>{
    this.setState({[e.target.name]: e.target.value});
};

//function for button "SIGN IN"
onLoginClick = async() => {
  const {username, password }=this.state;
  const errors = this.validateInputs(username, password);

if (Object.keys(errors).length>0){
  this.setState({errors});
} else{
  this.setState({errors:{}, loginError:""});
  console.log("Login " + username );

  try{
    const response = await fetch(`${BASE_URL}/sign-in`, {
method: 'POST',
headers:{
'Content-Type': 'application/json',
},
body: JSON.stringify({username, password }),
    });
    const data = await response.json();

    if(response.ok){
      localStorage.setItem('authToken', data.token);
      localStorage.setItem('username', username);
      console.log('Login successful: ', localStorage.getItem('authToken'));
     window.location.replace('/news');
    } else {
      this.setState({loginError: data.message || "Log In failed: Invalid username or password."})
      console.error('Login failes', data.message);
    }
  } catch(error){
    console.log('Error during Login:', error);
    this.setState({loginError: "Log In failed: Invalid username or password."});
  }
}
    };

render(){
  return (
    <div  className="login-page" >
        <Row className="w-50  justify-content-md-center">
            <Col >
              <Container className="login-container bg-light text-black text-center shadow p-4">
     <h1 className="text-center mb-5" >Login</h1>
     {this.state.loginError && (
     <div className="alert alert-danger" role="alert" onClose={() => this.setState({ loginError: "" })} dismissible>
      {this.state.loginError}
     </div>)}
      <Form>
    <Form.Group controlId="usernameId" className="mb-4 input-controller">
    
    <Form.Control
                  type="text"
                   size="lg"
                  name="username"
                  placeholder="Username"
                  value={this.state.username}
                  onChange={this.onChange}
                  isInvalid={!!this.state.errors.username}
                />
                <FormControl.Feedback type="invalid" >
                  {this.state.errors.username}
                </FormControl.Feedback>
</Form.Group>
              <Form.Group controlId="passwordId" className="mb-4 input-controller">
  
                <Form.Control
                  type="password"
                  size="lg"
                  name="password"
                  placeholder="Password"
                  value={this.state.password}
                  onChange={this.onChange}
                  isInvalid={!!this.state.errors.password}
                />
                <Form.Control.Feedback type="invalid" >
                  {this.state.errors.password}
                </Form.Control.Feedback>
              </Form.Group>
            <Button className="mt-4 login-button"  onClick={this.onLoginClick}>SIGN IN</Button>
            </Form>
            <p className="text-center mt-3"> Don't have account? &nbsp;<a class="link pr-3"  href="/sign-up">SignUp</a>
            </p>
            </Container>
  </Col>
   </Row> 
   
   </div>
  );
}
}
export default Login;