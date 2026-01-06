package com.example.gitly.data.local.repository

import com.example.gitly.data.local.dao.FavoriteRepoDao
import com.example.gitly.data.local.dao.FavoriteUserDao
import com.example.gitly.data.local.entity.FavoriteRepo
import com.example.gitly.data.local.entity.FavoriteUserEntity
import com.example.gitly.data.model.GitHubRepo
import com.example.gitly.data.model.GitHubUser
import kotlinx.coroutines.flow.Flow

class FavoritesRepository(
    private val favoriteRepoDao: FavoriteRepoDao,
    private val favoriteUserDao: FavoriteUserDao
) {
    
    // Repository operations
    fun getAllFavoriteRepos(): Flow<List<FavoriteRepo>> = favoriteRepoDao.getAllFavoriteRepos()
    
    suspend fun isFavoriteRepo(repoId: Long): Boolean = favoriteRepoDao.isFavorite(repoId)
    
    suspend fun addFavoriteRepo(repo: GitHubRepo) {
        val favoriteRepo = FavoriteRepo(
            id = repo.id,
            name = repo.name,
            fullName = repo.fullName,
            ownerLogin = repo.owner.login,
            ownerAvatarUrl = repo.owner.avatarUrl,
            ownerType = repo.owner.type,
            description = repo.description,
            isPrivate = repo.private,
            htmlUrl = repo.htmlUrl,
            language = repo.language,
            stargazersCount = repo.stargazersCount ?: 0,
            watchersCount = repo.watchersCount ?: 0,
            forksCount = repo.forksCount ?: 0,
            openIssuesCount = repo.openIssuesCount ?: 0,
            createdAt = repo.createdAt,
            updatedAt = repo.updatedAt,
            topics = repo.topics?.joinToString(","),
            visibility = repo.visibility,
            homepage = repo.homepage,
            archived = repo.archived,
            fork = repo.fork
        )
        favoriteRepoDao.insertFavoriteRepo(favoriteRepo)
    }
    
    suspend fun removeFavoriteRepo(repoId: Long) {
        favoriteRepoDao.deleteFavoriteRepoById(repoId)
    }
    
    suspend fun toggleFavoriteRepo(repo: GitHubRepo) {
        if (isFavoriteRepo(repo.id)) {
            removeFavoriteRepo(repo.id)
        } else {
            addFavoriteRepo(repo)
        }
    }
    
    // User operations
    fun getAllFavoriteUsers(): Flow<List<FavoriteUserEntity>> = favoriteUserDao.getAllFavoriteUsers()
    
    suspend fun isFavoriteUser(userId: Int): Boolean = favoriteUserDao.isFavorite(userId)
    
    suspend fun addFavoriteUser(user: GitHubUser) {
        val favoriteUser = FavoriteUserEntity(
            id = user.id,
            login = user.login,
            avatarUrl = user.avatar_url,
            htmlUrl = user.html_url,
            type = user.type,
            name = user.name,
            company = user.company,
            blog = user.blog,
            location = user.location,
            email = user.email,
            bio = user.bio,
            publicRepos = user.public_repos,
            publicGists = user.public_gists,
            followers = user.followers,
            following = user.following,
            createdAt = user.created_at,
            updatedAt = user.updated_at,
            twitterUsername = user.twitter_username
        )
        favoriteUserDao.insertFavoriteUser(favoriteUser)
    }
    
    suspend fun removeFavoriteUser(userId: Int) {
        favoriteUserDao.deleteFavoriteUserById(userId)
    }
    
    suspend fun toggleFavoriteUser(user: GitHubUser) {
        if (isFavoriteUser(user.id)) {
            removeFavoriteUser(user.id)
        } else {
            addFavoriteUser(user)
        }
    }
}
