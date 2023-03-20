import React, { useEffect, useRef, useState } from 'react';
import { Button, Card, Container } from 'react-bootstrap';
import { useNavigate, useParams } from 'react-router-dom';
import { useUser } from '../../UserProvider/UserProvider';

const UserNoFollowedList = (props) => {
    const user = useUser();
    const {userId} = useParams();
    const {pageName, pageId, pageDesc} = props;
    const navigate = useNavigate();
    const thumbRef = useRef(null);

    const [isImg, setIsImg] = useState(false);
    const [myurl, setMyurl] = useState();

    useEffect(() => {
        if(myurl){ thumbRef.current.src = myurl ; }
    }, [myurl])
    
    // /api/pages/${pageId}/thumbnails
    useEffect(() => {
        fetch(`/api/pageThumbnails/page/${pageId}/thumbnails`, {headers: {Authorization: `Bearer ${user.jwt}`}, method: "GET"})
          .then((thumbnailResponse) => { return thumbnailResponse.blob(); })
          .then((data) => {
            if(data.type.split("/")[1] != 'json'){
                setIsImg(true);
                setMyurl(URL.createObjectURL(data)); 
           }
            return data;
          })
          .catch((error) => {console.log(error);})
    }, [])

    function displayImg(){
        if(thumbRef != null && isImg){
            return  <div className='createdCard-img'>
            <Card.Img thumbnail variant="bottom" ref={thumbRef} /></div>
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
                    <Card.Title>Special title treatment</Card.Title>
                    <Card.Text>{pageDesc}</Card.Text>
                    
                    {displayImg()}
                    <div style={{position: "absolute", bottom: "10px"}}>
                        <Button className='createdCard-btn mt-3' variant="primary" onClick={handleClick}>Explore</Button>
                    </div>
                </Card.Body>
            </Card>
    </Container>
    );
};

export default UserNoFollowedList;