import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import React from 'react';
import { Nav } from 'react-bootstrap';
import { useNavigate, useParams } from 'react-router-dom';
import { useUser } from '../UserProvider/UserProvider';

function FriendNavbar(props) {
    const user = useUser();
    
    const {userId} = useParams();
    const navigate = useNavigate();
 
    return (
        <div>
            <Nav justify variant="tabs" className="custom-friendNav">
                <Nav.Item className='custom-friendItem'>
                    <Nav.Link href={`/users/${userId}/friends`} className='custom-friendLink'>
                    <FontAwesomeIcon icon="fa-solid fa-house" className='my-primary my-secondary' />
                    <span className='friendNavLink-text'>Home</span>
                    </Nav.Link>
                </Nav.Item>
                <Nav.Item className='custom-friendItem'>
                    <Nav.Link href={`/users/${userId}/friends/requests`} className='custom-friendLink'>
                    <FontAwesomeIcon icon="fa-solid fa-user-check" className='my-primary my-secondary' />
                    <span className='friendNavLink-text'>Requests</span></Nav.Link>
                </Nav.Item>
                <Nav.Item className='custom-friendItem'>
                    <Nav.Link href={`/users/${userId}/friends/addFriends`} className='custom-friendLink'>
                    <FontAwesomeIcon icon="fa-solid fa-user-plus" className='my-primary my-secondary' />
                    <span className='friendNavLink-text'>Add Friends</span></Nav.Link>
                </Nav.Item>
                <Nav.Item className='custom-friendItem'>
                    <Nav.Link href={`/users/${userId}/friends/allFriends`} className='custom-friendLink'>
                    <FontAwesomeIcon icon="fa-solid fa-users" className='my-primary my-secondary' />
                    <span className='friendNavLink-text'>Friends</span></Nav.Link>
                </Nav.Item>
            </Nav>
        </div>
    );
}

export default FriendNavbar;