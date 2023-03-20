import React, { useEffect, useState } from 'react';
import { Button, Card } from 'react-bootstrap';
import requestToPath from '../Service/fetchService';
import { useUser } from '../UserProvider/UserProvider';
import UserImage from './UserMedia/UserImage';
import UserVideo from './UserMedia/UserVideo';
import relativeTime from 'dayjs/plugin/relativeTime';
import dayjs from 'dayjs';
import { useNavigate } from 'react-router-dom';
import PostCommentModal from '../Modals/MyPosts/Comments/PostCommentModal';

const ViewDashPosts = (props) => {
    const user = useUser();
    const {eachPost, loggedUser} = props;
    const navigate = useNavigate();
    
    const [mediaType, setMediaType] = useState(null);
    const [currMedia, setCurrMedia] = useState(null);
    const [timeCollapsed, setTimeCollapsed] = useState(null);
    const [postLiked, setPostLiked] = useState(false);
    const [show, setShow] = useState(false);

    const postId = eachPost.id;

    const currUserId = eachPost.user.id;
    const firstname = eachPost.user.firstname;
    const lastname = eachPost.user.lastname;
    const createdOn = eachPost.creationDate;
    const postDesc = eachPost.description;
    
    function handleClose(){ setShow(false); }
    function handleShow(){ setShow(true); }
    
    useEffect(() => {
        requestToPath(`/api/posts/${postId}/postPhotos`, "GET", user.jwt)
            .then((mediaResponse) => {
                setMediaType(mediaResponse.type.split("/")[0]);
                setCurrMedia(mediaResponse);
            })
            .catch((error) => { console.log(error); })
    }, [])

    useEffect(() => {
        requestToPath(`/api/posts/${postId}/isLiked`, "GET", user.jwt)
            .then((likeResponse) => {setPostLiked(true);})
            .catch((error) => {console.log(error);})
    }, [])

    useEffect(() => {
        dayjs.extend(relativeTime);
        const tempTime = dayjs(createdOn).fromNow();
        setTimeCollapsed(tempTime);
    }, [createdOn])

    function displayMedia(){
        if(mediaType != null && currMedia.path != null){
            if(mediaType != 'video'){ return <UserImage postId = {postId} /> }
            return <UserVideo postId = {postId} />
        }
        return;
    }

    function handleClick(){
        navigate(`/users/${loggedUser}/user/${currUserId}/profile`)
    }

    function handleVisitBtn(){
        if(loggedUser != currUserId){
            return <div><Button onClick={handleClick}>View</Button></div>
        }
        return;
    }

    function increaseLike(){
        setPostLiked(true);
        requestToPath(`/api/posts/${postId}/increaseLike`, "POST", user.jwt)
            .then((likeResponse) => {console.log(likeResponse); setPostLiked(true);})
            .catch((error) => {console.log(error);})
    }

    function decreaseLike(){
        setPostLiked(false);
        requestToPath(`/api/posts/${postId}/decreaseLike`, "DELETE", user.jwt)
            .then((likeResponse) => {setPostLiked(false);})
            .catch((error) => {console.log(error);})
    }

    function displayLikeBtn(){
        if(postLiked){
            return <Button className='flex-fill me-1' onClick={() => {decreaseLike();}}>Like</Button>
        }
        return <Button className='flex-fill me-1' variant='secondary' onClick={() => {increaseLike()}}>Like</Button>
    }

    function displayCommentBtn(){
        return <Button className='flex-fill ms-1' variant='secondary' onClick={() => {handleShow()}}>Comments</Button>
    }

    return (
        <div className="mt-5" style={{display:"flex", flexDirection:"column", justifyContent:"center", alignItems:"center",}}>
            <Card className='custom-postCard'>  
                <Card.Body>
                    <div className='d-flex justify-content-between'>    
                        <div>
                            <Card.Title>{firstname} {lastname}</Card.Title>
                            <Card.Subtitle className="mb-2 text-muted">{timeCollapsed}</Card.Subtitle>
                        </div>
                        {handleVisitBtn()}
                    </div>
                    <Card.Text>
                        {postDesc}
                    </Card.Text>
                </Card.Body>
                {displayMedia()}

                <Card.Footer className='d-flex'>
                    {displayLikeBtn()}
                    {displayCommentBtn()}
                </Card.Footer>

                <PostCommentModal postId={postId} show={show} emitHandleClose={handleClose} />
            </Card>
        </div>
    );
};

export default ViewDashPosts;