import React, { useEffect, useRef, useState } from 'react';
import { Button, Card, Container } from 'react-bootstrap';
import { useNavigate, useParams } from 'react-router-dom';
import { useUser } from '../UserProvider/UserProvider';

import './Page/pages.css';

const CreatedCards = (props) => {
    const user = useUser();
    const {userId} = useParams();
    const {pageName, pageDesc, pageEmail, pagePhone, pageId} = props;
    // const [thumbnail, setThumbnail] = useState({});
    const [isImg, setIsImg] = useState(false);
    const [myurl, setMyurl] = useState();
    const navigate = useNavigate();
    const thumbRef = useRef(null);
    
    // /api/pages/${pageId}/thumbnails
    useEffect(() => {
        fetch(`/api/pageThumbnails/page/${pageId}/thumbnails`, {headers: {Authorization: `Bearer ${user.jwt}`}, method: "GET"})
          .then((thumbnailResponse) => {
            if(thumbnailResponse.status != 400 && thumbnailResponse.status != 404){
                return thumbnailResponse.blob();
            }
          })
          .then((data) => {
            if(data != null){
                setIsImg(true);
                setMyurl(URL.createObjectURL(data));
            }
            return data;
          })
          .catch((error) => { console.log(error); })
      }, [])

    useEffect(() => {
        if(myurl)
            thumbRef.current.src = myurl ;
    }, [myurl])

    function displayImg(){
        if(thumbRef != null && isImg){
            return  <div className='createdCard-img'>
                        <Card.Img variant="bottom" ref={thumbRef} alt="okay" />
                    </div>
        }
        return <div className='createdCard-img'>No thumbnail uploaded!</div>
    }

    function handleClick(){
        navigate(`/users/${userId}/pages/page/${pageId}`)
    }
      
    return (
        <Container className='mb-5' style={{height: "46vh"}}> 
            <Card style={{height: "100%"}}>
                <Card.Header as="h5">{pageName}</Card.Header>
                <Card.Body>
                    <Card.Title>This should be company name, and other is parent company</Card.Title>
                    <Card.Text>{pageDesc}</Card.Text>
                    
                    {displayImg()}

                    <div style={{position: "absolute", bottom: "10px"}}>
                        <Button variant="primary" onClick={handleClick}>Manage Page</Button>
                    </div>
                </Card.Body>
            </Card>
        </Container>
    );
};

export default CreatedCards;