import React, { useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import './Header.css';
import newsIcon from '../news/images/news-icon.png';

const Header = () => {
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const navigate = useNavigate();

  useEffect(() => {
    const token = localStorage.getItem('authToken');
    if(token){
      setIsAuthenticated(true);
    }
  }, []);

const handleSignIn = () =>{
  setIsAuthenticated(true);
  navigate("/news");
};

const handleSignOut = () =>{
  setIsAuthenticated(false);
  localStorage.removeItem('authToken');
  localStorage.removeItem('username');
  navigate("/");
};

  return (
    <header className="header ">
          <nav className="nav-container">
          <div className="nav-left">
          <img src={newsIcon} alt="Trash icon" className="news-icon" />
          <a className="navbar-brand" >News <br/> Management</a>
          <a className="nav-link "  href="/">Home</a>
          <a className="nav-link " aria-current="page" href="/news" onClick={handleSignIn}>News</a>
          <a className="nav-link" aria-current="page" href="/about-us">About Us</a>
          </div>
          <div className="nav-right">
            {!isAuthenticated?(
              <>
           <a className="nav-link right-align" href="/sign-in">Sign In</a>
           <a className="nav-link right-align" href="/sign-up">Sign Up</a>
          </> ):( 
            <a className="nav-link right-align" href="/" onClick={handleSignOut}>Sign Out</a>
            
          )}</div>
          </nav>
        
     
    </header>
  );
};

export default Header;