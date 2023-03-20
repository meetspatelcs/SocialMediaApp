import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import React from 'react';
import { Nav } from 'react-bootstrap';

function PageOtherNavBar(props) {

    
    return (
        
        <Nav variant="tabs" defaultActiveKey="#images">
            <Nav.Item>
                <Nav.Link href="#images">Images</Nav.Link>
            </Nav.Item>
            <Nav.Item>
                <Nav.Link eventKey="#videos" href="#videos">Video</Nav.Link>
            </Nav.Item>
        </Nav>
        
    );
}

export default PageOtherNavBar;

