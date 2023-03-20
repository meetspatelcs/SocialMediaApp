import React, { useEffect, useState } from 'react';
import { LazyLoadImage } from 'react-lazy-load-image-component';
import { useLocation, useParams } from 'react-router-dom';
import NavBar from '../NavBar/NavBar';
import { useUser } from '../UserProvider/UserProvider';
import myImg from '../MyPosts/low_poly_purple_abstract_art-wallpaper-1920x1080.jpg';
import ViewAboutMain from '../Components/MyPosts/About/ViewAboutMain';
import requestToPath from '../Service/fetchService';
import ViewVisitUserPosts from '../Cards/MyPosts/ViewVisitUserPosts';
import ViewMediaMain from '../Components/MyPosts/Media/ViewMediaMain';
import ViewFollowMain from '../Components/MyPosts/Follow/ViewFollowMain';
import VIewFriendsMain from '../Components/MyPosts/Friends/VIewFriendsMain';
import RequestsHandler from './RequestsHandler';

import '../Components/MyPosts/userProfile.css';

const ViewMyPosts = () => {
    const user = useUser();
    const {userId, visitUserId} = useParams();
    const {state} = useLocation();

    const [userPosts, setUserPosts] = useState([]);
    const [myFriends, setMyFriends] = useState([]);
    const [friendStatus, setFriendStatus] = useState('');

    useEffect(() => {
        if(state){ setFriendStatus(state.compStatus); }
    }, [state])

    useEffect(() => {
        requestToPath(`/api/posts/user/${visitUserId}/profile`, "GET", user.jwt)
            .then((myPostsData) => { setUserPosts(myPostsData); })
            .catch((error) => { console.log(error); })
    }, [visitUserId])
    
    useEffect(() => {
        requestToPath(`/api/friends/user/${userId}/all`, "GET", user.jwt)
            .then((myFriendResponse) => { setMyFriends(myFriendResponse); })
            .catch((error) => { console.log(error); })
    }, [visitUserId])

    function displayPosts(){
        if(userPosts != null){
            return userPosts.sort((a,b) => b.id > a.id ? 1 : -1)
                .map((eachPost) => {
                    return <div key={eachPost.id}><ViewVisitUserPosts eachMyPost={eachPost} /></div>
                })
        }
        return <div>No posts to Show!</div> 
    }
    
    return (
        <div style={{margin:"0", padding:"0"}}>
            <div className='custom-main' >
                <div> 
                    <div className='mb-2 userThumb-styl' >
                        <LazyLoadImage thumbnail="true" src={myImg}  />
                    </div>
                    <RequestsHandler userId={userId} visitUserId={visitUserId} friendStatus={friendStatus} />                    
                </div>
                <div className='custom-profileGrid justify-content-center'>
                    <ViewAboutMain />

                    <ViewMediaMain />

                    <ViewFollowMain />

                    <VIewFriendsMain myFriends={myFriends}/>
                </div>
                <div>
                    {displayPosts()}
                </div>
                <div style={{textAlign:"center"}}>
                    <br/>  Change this to something   <br/>  <br/>  <br/><br/><br/>
                </div>
            </div>
            <NavBar />
        </div>
    );
};

export default ViewMyPosts;