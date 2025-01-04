import  { Container, Navbar, Nav, Footer } from "react-bootstrap";
import React, { Component } from "react";
import { Link } from "react-router-dom";
import 'bootstrap/dist/css/bootstrap.min.css';
import Login from "./login/Login";


            class Home extends Component{
                render() {
                    return (
                      <Container >
                        <h1>Home</h1>
                        <p >
                          <Link to="/sign-in/">Login</Link>
                        </p>
                        <p>
                          <Link to="/sign-up">Sign up</Link>
                        </p>
                        <p>
                          <Link to="/dashboard">Dashboard</Link>
                        </p>
                      </Container>
                    );
                  }
                }
            
export default Home;
