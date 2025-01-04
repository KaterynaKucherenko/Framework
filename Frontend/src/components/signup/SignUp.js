import React, { Component } from 'react'
import { Link } from "react-router-dom";
import {Container,
    Button,
    Row,
    Col,
    Form,
    FormControl
  } from "react-bootstrap";
  import './SignUp.css';

  const BASE_URL = 'http://localhost:8082';

class SignUp extends Component{
    constructor (props) {
        super(props);
        this.state={
            password: "",
            username: "",
            errors:{
                password:"",
                username:""
                
            }
        };
    }

validateInputs = (username, password) => {
const errors = {};
if(!username){
  errors.username = "Username should not be empty."
} else if(username.length<3 || username.length>30){
  errors.username = "Username must be between 3 and 30 characters."
}

if(!password){
  errors.password = "Password should not be empty."
} else if (password.length<4 || password.length>30){
  errors.password = "Password must be between 4 and 30 characters."
}
return errors;
};

    onChange = e => {
        this.setState({ [e.target.name]: e.target.value});
    };

    //function for button "SIGN UP"
onSignUpClick = async () => {
const { username, password} = this.state;
const errors = this.validateInputs(username, password);

if(Object.keys(errors).length > 0){
    this.setState({errors});
} else{
    this.setState({errors:{}});
 console.log("Sign Up " + username);

 try{
const response = await fetch(`${BASE_URL}/sign-up`, {
method: 'POST',
headers: {
    'Content-Type': 'application/json',
},
body: JSON.stringify({ username, password }),
 });

 const data = await response.json();

 if(response.ok){
    localStorage.setItem('authToken', data.token);
    localStorage.setItem('username', username);
    console.log('Sign Up successful: ', localStorage.getItem('authtoken'));
    window.location.replace('/news');

      } else {
        console.error('Sign Up failed:', data.message);
      }
    } catch (error) {
      console.error('Error during sign-up:', error);
 }
}
};
    render(){
  return (

    <div className='signup-page'>
<Row className='w-50 justify-content-md-center'>
     <Col >
    <Container  className="signup-container bg-light text-black text-center shadow p-4">
    <h1 className="text-center mb-5">SignUp</h1>
<Form>
<Form.Group controlId="usernameId" className='mb-4 input-controller'>
<Form.Control 
    type = "text"
    size = "lg"
    name = "username"
    placeholder = "Username"
value = {this.state.username}
onChange={this.onChange}
isInvalid={!!this.state.errors.username}
/>
<FormControl.Feedback type = "invalid">
  {this.state.errors.username}
</FormControl.Feedback>
</Form.Group>
<Form.Group controlId="passwordId" className="mb-4 input-controller">
<Form.Control 
type = "password"
size = "lg"
name ="password"
placeholder = "Password"
value = {this.state.password}
onChange={this.onChange}
isInvalid={!!this.state.errors.password}
/>
<FormControl.Feedback type = "invalid"> {this.state.errors.password}</FormControl.Feedback>
</Form.Group>
<Button className="mt-4 signup-button"  onClick={this.onSignUpClick}>SIGN UP</Button>
</Form>
<p className="text-center mt-3">Already have account? &nbsp; <a class="link pr-3"  href="/sign-in"> Login </a>
</p>
 </Container> </Col> </Row>

   </div>
  );
}
}
export default SignUp;
