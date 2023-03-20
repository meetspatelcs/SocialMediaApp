import React, { useEffect, useState } from 'react';
import { Button, Card } from 'react-bootstrap';
import { useNavigate, useParams } from 'react-router-dom';

import '../../../Components/MyPosts/Follow/follow.css';
import { useUser } from '../../../UserProvider/UserProvider';

const ViewAllFollow = (props) => {
    const user = useUser();
    const {userId} = useParams();
    const {currPage} = props;
    const navigate = useNavigate();
    
    const [myurl, setMyUrl] = useState();

    const pageId = currPage.id;
    const pageName = currPage.compName;
    const pageBio = currPage.compDesc;
    const default_type = 'application/json';
    // TODO: This page must populate role of user on page if any by fetch service to userpage entity and pass role with navigate to explore button
    // /api/pages/${pageId}/thumbnails
    useEffect(() => {
        fetch(`/api/pageThumbnails/page/${pageId}/thumbnails`, {headers: {Authorization: `Bearer ${user.jwt}`}, method: "GET"})
          .then((thumbnailResponse) => { return thumbnailResponse.blob(); })
          .then((data) => {
            if(data.type != default_type){ setMyUrl(URL.createObjectURL(data)) }
            return data;
          })
          .catch((error) => {console.log(error);})
    }, []);

    function displayTumbnail(){
        if(myurl){
            return <Card.Img thumbnail={true} src={myurl} />
        }
        else{
            return <div>No thumbnail!</div>
        }
    }

    function displayVisitBtn(){
        return <Button className='mt-3' style={{width: "100%"}} variant="primary" 
            onClick={() => {navigate(`/users/${userId}/pages/page/${pageId}`)}}>Explore</Button>;
    }

    return (
        <Card>
            <Card.Header as="h5">{pageName}</Card.Header>
            <Card.Body>
                <Card.Title>Special title treatment</Card.Title>
                <Card.Text className='viewAllF-bio'>{pageBio}</Card.Text>
                {displayTumbnail()}
                {displayVisitBtn()}
            </Card.Body>
        </Card>
    );
};

export default ViewAllFollow;