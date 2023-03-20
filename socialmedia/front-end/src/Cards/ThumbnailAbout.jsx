import React from 'react';
import { Card } from 'react-bootstrap';
import OtherTab from '../Components/Pages/OtherTab';
import PageOtherNavBar from '../NavBar/PageOtherNavBar';

const ThumbnailAbout = (props) => {
    const {pageName, pageDesc, pageEmail, pagePhone} = props;
    const otherTab = window.location.href.split("#")[1];

    function isOtherTab(){
        if(otherTab != '' && otherTab != null){
            return <OtherTab otherTabVal={otherTab} />
        }
    }

    return (
        
            <Card>
                <Card.Header as="h2">{pageName}</Card.Header>
                <Card.Body>

                    <Card.Title as={"h3"}>About</Card.Title>
                    <Card.Text>
                        {pageDesc}
                    </Card.Text>
                    <hr/>
                    
                    <Card.Title as={"h5"}>Contact</Card.Title>
                    <Card.Text>
                        Email: {pageEmail} <br />
                        Phone: {pagePhone}
                    </Card.Text>  

                    <Card.Title as={"h5"}>Other</Card.Title>
                    
                        Links, Photo, Video.
                        <PageOtherNavBar />
                        {/* {otherTab ? <><OtherTab otherTabVal={otherTab} /></> : <></>} */}
                        {isOtherTab()}

                </Card.Body>
            </Card>
        
    );
};

export default ThumbnailAbout;