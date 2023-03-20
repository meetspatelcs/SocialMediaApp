
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import React, { useEffect, useState } from 'react';
import { Nav, Navbar } from 'react-bootstrap';
import { useNavigate } from 'react-router-dom';
import requestToPath from '../Service/fetchService';
import { useUser } from '../UserProvider/UserProvider';


function NavBar() {
    const user = useUser();
    const navigate = useNavigate();
    const[myInfo, setMyInfo] = useState({});

    useEffect(() =>{
        requestToPath("/api/users/myDetails", "GET", user.jwt)
        .then((myData) => {
           setMyInfo(myData);
        })
        .catch((error) => {console.log(error);})
    },[]);
    
    function handleDashClick(){
        const loggedUser = myInfo.id;
        navigate(`/dashboard`, {state:{loggedUser}});
    }
    
    return (
    <Navbar className='custom-navbar'>
        <Nav className='custom-navbarNav'>
            <div className='custom-navSpecial'></div>
            <Nav.Item className='custom-navItem'>
                <Nav.Link className='custom-navLink'>
                    <span className='logo'>Social Media</span>
                </Nav.Link>
            </Nav.Item>

            <Nav.Item className='custom-navItem'>
                <Nav.Link className='custom-navLink' href={`/dashboard`}>
                    <FontAwesomeIcon icon="fa-solid fa-house" className='my-primary my-secondary'/>
                    <span className='navLink-text'>Dashboard</span>
                </Nav.Link>
            </Nav.Item>

            <Nav.Item className='custom-navItem'>
                <Nav.Link className='custom-navLink' href={`/users/${myInfo.id}/myPosts`}>
                    <FontAwesomeIcon icon="fa-solid fa-photo-film" className='my-primary my-secondary' />
                    <span className='navLink-text'>My Posts</span>
                </Nav.Link>
            </Nav.Item>

            <Nav.Item className='custom-navItem'>
                <Nav.Link className='custom-navLink' href={`/users/${myInfo.id}/pages/mypages`}>
                    <FontAwesomeIcon icon="fa-solid fa-pager" className='my-primary my-secondary' />
                    <span className='navLink-text'>Pages</span>
                </Nav.Link>
            </Nav.Item>
            
            {/* add the path to friends pages */}
            <Nav.Item className='custom-navItem'>
                <Nav.Link className='custom-navLink' href={`/users/${myInfo.id}/friends`} >
                    <FontAwesomeIcon icon="fa-solid fa-user-group" className='my-primary my-secondary' />
                    <span className='navLink-text'>Friends</span> 
                </Nav.Link>
            </Nav.Item>

            <Nav.Item className='custom-navItem'>
                <Nav.Link className='custom-navLink'>
                    <FontAwesomeIcon icon="fa-solid fa-gears" className='my-primary my-secondary' />
                    <span className='navLink-text'>Settings</span> 
                </Nav.Link>
            </Nav.Item>

            <Nav.Item className='custom-navItem'>
                <Nav.Link className='custom-navLink' onClick={()=>{user.setJwt(null); navigate('/');}}>
                    <FontAwesomeIcon icon="fa-solid fa-right-from-bracket" className='my-primary my-secondary' />
                    <span className='navLink-text'>Log out</span> 
                </Nav.Link>
            </Nav.Item>
        </Nav>
    </Navbar>
    );
}

export default NavBar;