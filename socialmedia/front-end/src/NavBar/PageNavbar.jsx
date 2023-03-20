import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import React from 'react';
import { Nav } from 'react-bootstrap';
import { useNavigate, useParams } from 'react-router-dom';
import { useUser } from '../UserProvider/UserProvider';

function PageNavbar(props) {
    const user = useUser();
    
    const {userId} = useParams();
    const navigate = useNavigate();

    return (
        <div>
            <Nav justify variant="tabs" className="custom-friendNav">
                <Nav.Item className='custom-friendItem'>
                    <Nav.Link className='custom-friendLink' href={`/users/${userId}/pages/mypages`}>
                    <FontAwesomeIcon icon="fa-solid fa-house" className='my-primary my-secondary' />
                    <span className='friendNavLink-text'>My Pages</span>
                    </Nav.Link>
                </Nav.Item>
                <Nav.Item className='custom-friendItem'>
                    <Nav.Link className='custom-friendLink' href={`/users/${userId}/pages`}>
                    <FontAwesomeIcon icon="fa-solid fa-user-check" className='my-primary my-secondary' />
                    <span className='friendNavLink-text'>Following Pages</span></Nav.Link>
                </Nav.Item>
                <Nav.Item className='custom-friendItem'>
                    <Nav.Link className='custom-friendLink' href={`/users/${userId}/pages/addPages`}>
                    <FontAwesomeIcon icon="fa-solid fa-user-check" className='my-primary my-secondary' />
                    <span className='friendNavLink-text'>Follow Pages</span></Nav.Link>
                </Nav.Item>
              
                {/* <Nav.Item className='custom-friendItem'>
                    <Nav.Link className='custom-friendLink'>
                    <FontAwesomeIcon icon="fa-solid fa-user-plus" className='my-primary my-secondary' />
                    <span className='friendNavLink-text'>Add Friends</span></Nav.Link>
                </Nav.Item>
                <Nav.Item className='custom-friendItem'>
                    <Nav.Link className='custom-friendLink'>
                    <FontAwesomeIcon icon="fa-solid fa-users" className='my-primary my-secondary' />
                    <span className='friendNavLink-text'>Friends</span></Nav.Link>
                </Nav.Item> */}
            </Nav>
        </div>
    );
}

export default PageNavbar;