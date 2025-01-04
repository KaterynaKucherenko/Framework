import React, { Component } from 'react';
import { BrowserRouter, Route, Routes, Link } from "react-router-dom";
import Header from './components/fixedSections/Header';
import Footer from './components/fixedSections/Footer';
import SignUp from "./components/signup/SignUp.js";
import Login from "./components/login/Login";
import Dashboard from "./components/dashboard/Dashboard";
import Home from "./components/Home"; 
import NewsPage from './components/news/NewsPage.js';


class App extends Component {
  render() {
    return (
 <BrowserRouter >
   <div className="app-container">
          <Header />
          <div className="main-content">
          <Routes>
            <Route path="/sign-up" element={<SignUp />} />
            <Route path="/sign-in" element={<Login />} />
            <Route path="/news" element={<NewsPage />} />
            <Route path="/dashboard" element={<Dashboard />} />
            <Route exact path="/" element={<Home />} />
            <Route path="*" element={<div>Ups</div>} />
          </Routes>
          </div>
          
          <Footer />
        </div>
      
         </BrowserRouter>
     
    );
  }
}

export default App;
